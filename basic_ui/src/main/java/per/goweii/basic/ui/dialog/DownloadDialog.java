package per.goweii.basic.ui.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.LayerManager;
import per.goweii.basic.ui.R;
import per.goweii.basic.utils.DownloadUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.file.FileUtils;


/**
 * 版本更新弹窗
 *
 * @author Cuizhen
 * @date 2018/8/6-上午9:17
 */
public class DownloadDialog {

    private final Activity mActivity;
    private AnyLayer mAnyLayer = null;
    private final boolean isForce;
    private boolean isAutoInstall = true;

    private ProgressBar progressBar;
    private TextView tvProgress;
    private TextView tvApkSize;
    private TextView tvState;
    private File mApk;

    public static DownloadDialog with(Activity activity, boolean isForce, String url) {
        return new DownloadDialog(activity, isForce, url);
    }

    private DownloadDialog(Activity activity, boolean isForce, String url) {
        this.mActivity = activity;
        this.isForce = isForce;
        showDialog();
        startDownload(url);
    }

    public DownloadDialog setAutoInstall(boolean autoInstall) {
        isAutoInstall = autoInstall;
        return this;
    }

    private void startDownload(String url) {
        DownloadUtils.download(url, new DownloadUtils.DownloadListener() {
            @Override
            public void onPreExecute() {
                preDownload();
            }

            @Override
            public void onDownloadLength(int length) {
                if (tvApkSize != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvApkSize.setText(FileUtils.formatSize(length));
                        }
                    });
                }
            }

            @Override
            public void onProgressUpdate(int progress) {
                setProgress(progress);
            }

            @Override
            public void onPostExecute(File apk) {
                mApk = apk;
                if (isAutoInstall && tvState != null) {
                    tvState.performClick();
                }
            }
        });
    }

    private void showDialog() {
        mAnyLayer = AnyLayer.with(mActivity)
                .contentView(R.layout.basic_ui_dialog_download)
                .cancelableOnTouchOutside(false)
                .cancelableOnClickKeyBack(false)
                .bindData(new LayerManager.IDataBinder() {
                    @Override
                    public void bind(AnyLayer anyLayer) {
                        progressBar = anyLayer.getView(R.id.basic_ui_pb_dialog_download);
                        tvProgress = anyLayer.getView(R.id.basic_ui_tv_dialog_download_progress);
                        tvApkSize = anyLayer.getView(R.id.basic_ui_tv_dialog_download_apk_size);
                        tvState = anyLayer.getView(R.id.basic_ui_tv_dialog_download_state);
                    }
                })
                .backgroundColorRes(R.color.dialog_bg)
                .gravity(Gravity.CENTER)
                .onClick(R.id.basic_ui_tv_dialog_download_state, new LayerManager.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View view) {
                        if (mApk == null) {
                            return;
                        }
                        if (!isForce) {
                            dismiss();
                        }
                        DownloadUtils.installApk(mActivity, mApk);
                    }
                });
        mAnyLayer.show();
    }

    private void preDownload() {
        if (progressBar != null) {
            progressBar.setMax(100);
            progressBar.setProgress(0);
        }
        if (tvApkSize != null) {
            tvApkSize.setText("0B");
        }
        if (tvProgress != null) {
            tvProgress.setText("0");
        }
        if (tvState != null) {
            tvState.setText(R.string.basic_ui_dialog_download_state_downloading);
        }
    }

    private void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        if (tvProgress != null) {
            tvProgress.setText("" + progress);
        }
        if (progress >= 100) {
            if (tvState != null) {
                tvState.setText(R.string.basic_ui_dialog_download_state_install);
                tvState.setTextColor(ResUtils.getColor(R.color.text_main));
            }
        }
    }

    private void dismiss() {
        if (mAnyLayer != null) {
            mAnyLayer.dismiss();
        }
    }
}
