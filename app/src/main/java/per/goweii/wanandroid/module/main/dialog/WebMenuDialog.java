package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.view.View;

import per.goweii.anylayer.Alignment;
import per.goweii.anylayer.AnimHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.LayerManager;
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
        AnyLayer.target(target)
                .contentView(R.layout.dialog_web_menu)
                .alignment(Alignment.Direction.VERTICAL, Alignment.Horizontal.ALIGN_RIGHT, Alignment.Vertical.BELOW, false)
                .contentAnim(new LayerManager.IAnim() {
                    @Override
                    public Animator inAnim(View target) {
                        return AnimHelper.createDelayedZoomInAnim(target, 1F, 0F);
                    }

                    @Override
                    public Animator outAnim(View target) {
                        return AnimHelper.createDelayedZoomOutAnim(target, 1F, 0F);
                    }
                })
                .onClickToDismiss(new LayerManager.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
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
