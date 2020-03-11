package per.goweii.basic.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.ui.R;
import per.goweii.basic.utils.listener.SimpleCallback;

/**
 * @author Cuizhen
 * @date 2018/6/21-上午10:00
 */
public class TipDialog {

    private final Context context;
    private CharSequence title;
    private CharSequence msg;
    private CharSequence yesText;
    private CharSequence noText;
    private boolean singleBtnYes = false;
    private boolean cancelable = true;
    private SimpleCallback<Void> callbackYes = null;
    private SimpleCallback<Void> callbackNo = null;
    private SimpleCallback<Void> onDismissListener = null;
    private DialogLayer mDialogLayer;

    public static TipDialog with(Context context) {
        return new TipDialog(context);
    }

    private TipDialog(Context context) {
        this.context = context;
        mDialogLayer = AnyLayer.dialog(context);
        mDialogLayer.contentView(R.layout.basic_ui_dialog_tip)
                .gravity(Gravity.CENTER)
                .backgroundDimDefault()
                .cancelableOnTouchOutside(cancelable)
                .cancelableOnClickKeyBack(cancelable)
                .onVisibleChangeListener(new Layer.OnVisibleChangeListener() {
                    @Override
                    public void onShow(Layer layer) {
                    }

                    @Override
                    public void onDismiss(Layer layer) {
                        if (onDismissListener != null) {
                            onDismissListener.onResult(null);
                        }
                    }
                })
                .bindData(new Layer.DataBinder() {
                    @Override
                    public void bindData(Layer layer) {
                        TextView tvYes = layer.getView(R.id.basic_ui_tv_dialog_tip_yes);
                        TextView tvNo = layer.getView(R.id.basic_ui_tv_dialog_tip_no);
                        View vLine = layer.getView(R.id.basic_ui_v_dialog_tip_line);

                        if (singleBtnYes) {
                            tvNo.setVisibility(View.GONE);
                            vLine.setVisibility(View.GONE);
                        } else {
                            tvNo.setVisibility(View.VISIBLE);
                            vLine.setVisibility(View.VISIBLE);
                            if (noText != null) {
                                tvNo.setText(noText);
                            } else {
                                tvNo.setText(R.string.basic_ui_dialog_btn_no);
                            }
                        }

                        if (yesText != null) {
                            tvYes.setText(yesText);
                        } else {
                            tvYes.setText(R.string.basic_ui_dialog_btn_yes);
                        }

                        TextView tvTitle = layer.getView(R.id.basic_ui_tv_dialog_tip_title);
                        if (title == null) {
                            tvTitle.setVisibility(View.GONE);
                        } else {
                            tvTitle.setVisibility(View.VISIBLE);
                            tvTitle.setText(title);
                        }

                        TextView tvContent = layer.getView(R.id.basic_ui_tv_dialog_tip_content);
                        tvContent.setText(msg);
                    }
                })
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        if (callbackYes != null) {
                            callbackYes.onResult(null);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_tip_yes)
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        if (callbackNo != null) {
                            callbackNo.onResult(null);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_tip_no);
    }

    public TipDialog yesText(CharSequence yesText) {
        this.yesText = yesText;
        return this;
    }

    public TipDialog yesText(@StringRes int yesText) {
        this.yesText = context.getString(yesText);
        return this;
    }

    public TipDialog noText(CharSequence noText) {
        this.noText = noText;
        return this;
    }

    public TipDialog noText(@StringRes int noText) {
        this.noText = context.getString(noText);
        return this;
    }

    public TipDialog title(CharSequence title) {
        this.title = title;
        return this;
    }

    public TipDialog title(@StringRes int title) {
        this.title = context.getString(title);
        return this;
    }

    public TipDialog message(CharSequence msg) {
        this.msg = msg;
        return this;
    }

    public TipDialog message(@StringRes int msg) {
        this.msg = context.getString(msg);
        return this;
    }

    public TipDialog singleYesBtn() {
        singleBtnYes = true;
        return this;
    }

    public TipDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public TipDialog onYes(SimpleCallback<Void> callback) {
        callbackYes = callback;
        return this;
    }

    public TipDialog onNo(SimpleCallback<Void> callback) {
        callbackNo = callback;
        return this;
    }

    public TipDialog onDismissListener(SimpleCallback<Void> onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public void dismiss() {
        if (mDialogLayer != null) {
            mDialogLayer.dismiss();
        }
    }

    public void show() {
        if (mDialogLayer != null) {
            mDialogLayer.show();
        }
    }
}
