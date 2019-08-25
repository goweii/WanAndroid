package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.View;

import per.goweii.anylayer.Align;
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

    public static void show(@NonNull View target, @NonNull OnMenuClickListener listener) {
        AnyLayer.popup(target)
                .direction(Align.Direction.VERTICAL)
                .horizontal(Align.Horizontal.ALIGN_RIGHT)
                .vertical(Align.Vertical.BELOW)
                .inside(false)
                .contentView(R.layout.dialog_web_menu)
                .contentAnimator(new Layer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(View target) {
                        return AnimatorHelper.createDelayedZoomInAnim(target, 1F, 0F);
                    }

                    @Override
                    public Animator createOutAnimator(View target) {
                        return AnimatorHelper.createDelayedZoomOutAnim(target, 1F, 0F);
                    }
                })
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        switch (v.getId()) {
                            default:
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
                        }
                    }
                }, R.id.dialog_web_menu_tv_collect, R.id.dialog_web_menu_tv_read_later, R.id.dialog_web_menu_tv_browser)
                .show();
    }

    public interface OnMenuClickListener{
        void onCollect();
        void onReadLater();
        void onBrowser();
    }

}
