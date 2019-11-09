package per.goweii.wanandroid.module.main.activity;

import android.animation.Animator;
import android.view.View;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.PopupLayer;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/11/9
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebQuickDialog extends PopupLayer {

    public WebQuickDialog(View targetView, OnQuickClickListener onQuickClickListener) {
        super(targetView);
        contentView(R.layout.dialog_web_quick);
        backgroundColorRes(R.color.dialog_bg);
        outsideInterceptTouchEvent(false);
        interceptKeyEvent(false);
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(View target) {
                return AnimatorHelper.createTopInAnim(target);
            }

            @Override
            public Animator createOutAnimator(View target) {
                return AnimatorHelper.createTopOutAnim(target);
            }
        });
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                if (onQuickClickListener != null) {
                    onQuickClickListener.onCopyLink();
                }
            }
        }, R.id.dialog_web_quick_tv_copy_link);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                if (onQuickClickListener != null) {
                    onQuickClickListener.onBrowser();
                }
            }
        }, R.id.dialog_web_quick_tv_browser);
    }

    public interface OnQuickClickListener {
        void onCopyLink();

        void onBrowser();
    }
}
