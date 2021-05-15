package per.goweii.wanandroid.module.login.dialog;

import android.animation.Animator;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.anylayer.utils.AnimatorHelper;
import per.goweii.basic.utils.RegexUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * GitHub: https://github.com/goweii
 */
public class EmailInputDialog extends DialogLayer {

    private final SimpleCallback<String> mCallback;

    public EmailInputDialog(Context context, SimpleCallback<String> callback) {
        super(context);
        mCallback = callback;
        contentView(R.layout.dialog_email_input);
        backgroundDimDefault();
        cancelableOnClickKeyBack(false);
        cancelableOnTouchOutside(false);
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(@NonNull View target) {
                return AnimatorHelper.createBottomAlphaInAnim(target);
            }

            @Override
            public Animator createOutAnimator(@NonNull View target) {
                return AnimatorHelper.createBottomAlphaOutAnim(target);
            }
        });
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                EditText et_email = requireView(R.id.dialog_email_input_et_email);
                mCallback.onResult(et_email.getText().toString());
                dismiss();
            }
        }, R.id.dialog_email_input_tv_sure);
        compatSoftInput(true, R.id.dialog_email_input_et_email);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        EditText et_email = getView(R.id.dialog_email_input_et_email);
        TextView tv_tip = getView(R.id.dialog_email_input_tv_tip);
        TextView tv_sure = getView(R.id.dialog_email_input_tv_sure);
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(et_email.getText().toString())) {
                    tv_tip.setText("");
                    tv_sure.setEnabled(false);
                    tv_sure.setAlpha(0.6F);
                }
                if (RegexUtils.matchEmail(s.toString())) {
                    tv_tip.setText("");
                    tv_sure.setEnabled(true);
                    tv_sure.setAlpha(1F);
                } else {
                    tv_tip.setText("邮箱地址不合法");
                    tv_sure.setEnabled(false);
                    tv_sure.setAlpha(0.6F);
                }
            }
        });
        et_email.setText("");
    }
}
