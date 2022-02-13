package per.goweii.wanandroid.module.main.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.coder.MD5Coder;
import per.goweii.basic.utils.file.CacheUtils;
import per.goweii.basic.utils.file.FileUtils;
import per.goweii.rxhttp.download.DownloadInfo;
import per.goweii.rxhttp.download.RxDownload;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.InstallApkActivity;


/**
 * 版本更新弹窗
 *
 * @author Cuizhen
 * @date 2018/8/6-上午9:17
 */
public class DownloadDialog {

    private final Activity mActivity;
    private Layer mAnyLayer = null;
    private final boolean isForce;
    private final String url;
    private final String urlBackup;
    private boolean isAutoInstall = true;

    private ProgressBar progressBar;
    private TextView tvProgress;
    private TextView tvApkSize;
    private TextView tvState;
    private TextView tvSpeed;
    private TextView tvLine;
    private File mApk;
    private RxDownload mRxDownload;

    private int retryCount = 0;

    private OnDismissListener mOnDismissListener;

    public static DownloadDialog with(Activity activity,
                                      boolean isForce,
                                      String url,
                                      String urlBackup,
                                      OnDismissListener onDismissListener) {
        return new DownloadDialog(activity, isForce, url, urlBackup, onDismissListener);
    }

    private DownloadDialog(Activity activity,
                           boolean isForce,
                           String url,
                           String urlBackup,
                           OnDismissListener onDismissListener) {
        this.mActivity = activity;
        this.url = url;
        this.urlBackup = urlBackup;
        this.isForce = isForce;
        this.mOnDismissListener = onDismissListener;
        showDialog();
        startDownload(this.url);
    }

    public DownloadDialog setAutoInstall(boolean autoInstall) {
        isAutoInstall = autoInstall;
        return this;
    }

    private void startDownload(String url) {
        DownloadInfo info = DownloadInfo.create(url, CacheUtils.getCacheDir(), MD5Coder.encode(url) + ".apk");
        mRxDownload = RxDownload.create(info)
                .setProgressListener(new RxDownload.ProgressListener() {
                    @Override
                    public void onProgress(float progress, long downloadLength, long contentLength) {
                        setProgress((int) (progress * 100));
                    }
                })
                .setDownloadListener(new RxDownload.DownloadListener() {
                    @Override
                    public void onStarting(DownloadInfo info) {
                        preDownload();
                    }

                    @Override
                    public void onDownloading(DownloadInfo info) {
                        if (tvApkSize != null) {
                            tvApkSize.setText(FileUtils.formatSize(info.contentLength));
                        }
                        if (tvLine != null) {
                            tvLine.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onStopped(DownloadInfo info) {
                    }

                    @Override
                    public void onCanceled(DownloadInfo info) {
                    }

                    @Override
                    public void onCompletion(DownloadInfo info) {
                        mApk = new File(info.saveDirPath, info.saveFileName);
                        if (isAutoInstall && tvState != null) {
                            tvState.performClick();
                        }
                    }

                    @Override
                    public void onError(DownloadInfo info, Throwable e) {
                        FileUtils.delete(new File(info.saveDirPath, info.saveFileName));
                        retryCount++;
                        if (retryCount <= 3) {
                            startDownload(DownloadDialog.this.url);
                        } else if (retryCount <= 6) {
                            startDownload(DownloadDialog.this.urlBackup);
                        } else {
                            ToastMaker.showShort("下载失败，可前往酷安手动更新");
                            dismiss();
                        }
                    }
                })
                .setSpeedListener(new RxDownload.SpeedListener() {
                    @Override
                    public void onSpeedChange(float bytesPerSecond, String speedFormat) {
                        if (tvSpeed != null) {
                            tvSpeed.setText(speedFormat);
                        }
                    }
                });
        mRxDownload.start();
    }

    private void showDialog() {
        mAnyLayer = AnyLayer.dialog(mActivity)
                .setContentView(R.layout.dialog_download)
                .setBackgroundDimDefault()
                .setGravity(Gravity.CENTER)
                .setCancelableOnTouchOutside(false)
                .setCancelableOnClickKeyBack(false)
                .addOnBindDataListener(new Layer.OnBindDataListener() {
                    @Override
                    public void onBindData(@NonNull Layer layer) {
                        ImageView ivClose = layer.requireView(R.id.iv_dialog_download_close);
                        if (isForce) {
                            ivClose.setVisibility(View.GONE);
                        } else {
                            ivClose.setVisibility(View.VISIBLE);
                        }
                        progressBar = layer.requireView(R.id.pb_dialog_download);
                        tvProgress = layer.requireView(R.id.tv_dialog_download_progress);
                        tvApkSize = layer.requireView(R.id.tv_dialog_download_apk_size);
                        tvState = layer.requireView(R.id.tv_dialog_download_state);
                        tvSpeed = layer.requireView(R.id.tv_dialog_download_speed);
                        tvLine = layer.requireView(R.id.tv_dialog_download_line);
                    }
                })
                .addOnClickListener(new Layer.OnClickListener() {
                    @Override
                    public void onClick(@NonNull Layer layer, @NonNull View v) {
                        if (mApk == null) {
                            return;
                        }
                        if (!isForce) {
                            dismiss();
                        }
                        installApk();
                    }
                }, R.id.tv_dialog_download_state)
                .addOnClickListener(new Layer.OnClickListener() {
                    @Override
                    public void onClick(@NonNull Layer layer, @NonNull View v) {
                        if (!isForce) {
                            dismiss();
                        }
                    }
                }, R.id.iv_dialog_download_close)
                .addOnDismissListener(new Layer.OnDismissListener() {
                    @Override
                    public void onPreDismiss(@NonNull Layer layer) {
                    }

                    @Override
                    public void onPostDismiss(@NonNull Layer layer) {
                        if (mOnDismissListener != null) {
                            mOnDismissListener.onDismiss();
                        }
                    }
                });
        mAnyLayer.show();
    }

    private void installApk() {
        InstallApkActivity.start(mActivity, mApk);
    }

    private void preDownload() {
        if (progressBar != null) {
            progressBar.setMax(100);
            progressBar.setProgress(0);
        }
        if (tvApkSize != null) {
            tvApkSize.setText("");
        }
        if (tvProgress != null) {
            tvProgress.setText("");
        }
        if (tvState != null) {
            tvState.setText(R.string.basic_ui_dialog_download_state_downloading);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        if (tvProgress != null) {
            tvProgress.setText(progress + "%");
        }
        if (progress >= 100) {
            if (tvState != null) {
                tvState.setText(R.string.basic_ui_dialog_download_state_install);
                tvState.setTextColor(ResUtils.getThemeColor(tvState, R.attr.colorTextMain));
            }
        }
    }

    private void dismiss() {
        if (mRxDownload != null) {
            mRxDownload.stop();
        }
        if (mAnyLayer != null) {
            mAnyLayer.dismiss();
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
