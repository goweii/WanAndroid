package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebMenuDialog {

    public static void show(@NonNull Context context, @NonNull OnMenuClickListener listener) {
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_web_menu)
                .gravity(Gravity.BOTTOM)
                .backgroundColorRes(R.color.dialog_bg)
                .contentAnimator(new Layer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(View target) {
                        return AnimatorHelper.createBottomInAnim(target);
                    }

                    @Override
                    public Animator createOutAnimator(View target) {
                        return AnimatorHelper.createBottomOutAnim(target);
                    }
                })
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        switch (v.getId()) {
                            default:
                                break;
                            case R.id.dialog_web_menu_tv_share:
                                if (UserUtils.getInstance().doIfLogin(v.getContext())) {
                                    listener.onShare();
                                }
                                break;
                            case R.id.dialog_web_menu_tv_collect:
                                if (UserUtils.getInstance().doIfLogin(v.getContext())) {
                                    listener.onCollect();
                                }
                                break;
                            case R.id.dialog_web_menu_tv_read_later:
                                listener.onReadLater();
                                break;
                            case R.id.dialog_web_menu_tv_browser:
                                listener.onBrowser();
                                break;
                            case R.id.dialog_web_menu_tv_close_activity:
                                listener.onCloseActivity();
                                break;
                        }
                    }
                                  }, R.id.dialog_web_menu_iv_close,
                        R.id.dialog_web_menu_tv_share,
                        R.id.dialog_web_menu_tv_read_later,
                        R.id.dialog_web_menu_tv_browser,
                        R.id.dialog_web_menu_tv_collect,
                        R.id.dialog_web_menu_tv_close_activity)
                .show();
    }

    public interface OnMenuClickListener {
        void onShare();

        void onCollect();

        void onReadLater();

        void onBrowser();

        void onCloseActivity();
    }

}
