package per.goweii.wanandroid.module.mine.dialog;

import android.animation.Animator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AddHostDialog {

    public static void show(Context context, SimpleCallback<String> callback) {
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_add_host)
                .contentAnimator(new Layer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(View target) {
                        return AnimatorHelper.createTopInAnim(target);
                    }

                    @Override
                    public Animator createOutAnimator(View target) {
                        return AnimatorHelper.createTopOutAnim(target);
                    }
                })
                .avoidStatusBar(true)
                .backgroundColorRes(R.color.dialog_bg)
                .cancelableOnClickKeyBack(true)
                .cancelableOnTouchOutside(true)
                .gravity(Gravity.TOP)
                .onDismissListener(new Layer.OnDismissListener() {
                    @Override
                    public void onDismissing(Layer layer) {
                        EditText et_host = layer.getView(R.id.dialog_add_host_et_host);
                        InputMethodUtils.hide(et_host);
                    }

                    @Override
                    public void onDismissed(Layer layer) {
                    }
                })
                .onClickToDismiss(R.id.dialog_add_host_tv_no)
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        EditText et_host = layer.getView(R.id.dialog_add_host_et_host);
                        if (callback != null) {
                            String host = et_host.getText().toString();
                            callback.onResult(host);
                        }
                    }
                }, R.id.dialog_add_host_tv_yes)
                .show();
    }

}
