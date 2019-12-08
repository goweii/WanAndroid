package per.goweii.basic.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.ui.R;

/**
 * 版本更新弹窗
 *
 * @author Cuizhen
 * @date 2018/8/6-上午9:17
 */
public class UpdateDialog {

    private final Context mContext;

    private String mUrl = null;
    private String mUrlBackup = null;
    private int mVersionCode = 0;
    private String mVersionName = null;
    private String mTime = null;
    private String mDescription = null;
    private boolean mForce = false;

    private OnDismissListener mOnDismissListener = null;
    private OnUpdateListener mOnUpdateListener = null;

    public static UpdateDialog with(Context context) {
        return new UpdateDialog(context);
    }

    private UpdateDialog(Context context) {
        this.mContext = context;
    }

    public UpdateDialog setUrl(String url) {
        mUrl = url;
        return this;
    }

    public UpdateDialog setUrlBackup(String url) {
        mUrlBackup = url;
        return this;
    }

    public UpdateDialog setForce(boolean force) {
        mForce = force;
        return this;
    }

    public UpdateDialog setVersionCode(int versionCode) {
        mVersionCode = versionCode;
        return this;
    }

    public UpdateDialog setVersionName(String versionName) {
        this.mVersionName = versionName;
        return this;
    }

    public UpdateDialog setTime(String time) {
        mTime = time;
        return this;
    }

    public UpdateDialog setDescription(String description) {
        this.mDescription = description;
        return this;
    }

    public UpdateDialog setOnUpdateListener(OnUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;
        return this;
    }

    public UpdateDialog setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public void show() {
        AnyLayer.dialog(mContext)
                .contentView(R.layout.basic_ui_dialog_update)
                .backgroundDimDefault()
                .gravity(Gravity.CENTER)
                .cancelableOnTouchOutside(!mForce)
                .cancelableOnClickKeyBack(!mForce)
                .bindData(new Layer.DataBinder() {
                    @Override
                    public void bindData(Layer layer) {
                        final View vLine = layer.getView(R.id.basic_ui_v_dialog_update_line);
                        final TextView tvNo = layer.getView(R.id.basic_ui_tv_dialog_update_no);
                        final TextView tvVersionName = layer.getView(R.id.basic_ui_tv_dialog_update_version_name);
                        final TextView tvTime = layer.getView(R.id.basic_ui_tv_dialog_update_time);
                        final TextView tvDescription = layer.getView(R.id.basic_ui_tv_dialog_update_description);

                        if (TextUtils.isEmpty(mVersionName)) {
                            tvVersionName.setVisibility(View.GONE);
                        } else {
                            tvVersionName.setVisibility(View.VISIBLE);
                            tvVersionName.setText(mVersionName);
                        }
                        if (TextUtils.isEmpty(mTime)) {
                            tvTime.setVisibility(View.GONE);
                        } else {
                            tvTime.setVisibility(View.VISIBLE);
                            tvTime.setText(mTime);
                        }
                        tvDescription.setText(mDescription);
                        tvDescription.setMovementMethod(ScrollingMovementMethod.getInstance());

                        if (mForce) {
                            tvNo.setVisibility(View.GONE);
                            vLine.setVisibility(View.GONE);
                        } else {
                            tvNo.setVisibility(View.VISIBLE);
                            vLine.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        if (mOnUpdateListener != null) {
                            mOnUpdateListener.onDownload(mUrl, mUrlBackup, mForce);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_update_yes)
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        if (mOnUpdateListener != null) {
                            mOnUpdateListener.onIgnore(mVersionCode);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_update_no)
                .onDismissListener(new Layer.OnDismissListener() {
                    @Override
                    public void onDismissing(Layer layer) {

                    }

                    @Override
                    public void onDismissed(Layer layer) {
                        if (mOnDismissListener != null) {
                            mOnDismissListener.onDismiss();
                        }
                    }
                })
                .show();
    }

    public interface OnUpdateListener {
        void onDownload(String url, String urlBackup, boolean isForce);
        void onIgnore(int versionCode);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
