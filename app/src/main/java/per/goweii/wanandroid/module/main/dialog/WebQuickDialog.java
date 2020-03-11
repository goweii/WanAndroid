package per.goweii.wanandroid.module.main.dialog;

import android.view.View;

import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.PopupLayer;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/11/9
 * GitHub: https://github.com/goweii
 */
public class WebQuickDialog extends PopupLayer {

    public WebQuickDialog(View targetView, OnQuickClickListener onQuickClickListener) {
        super(targetView);
        contentView(R.layout.dialog_web_quick);
        outsideInterceptTouchEvent(false);
        interceptKeyEvent(false);
        backgroundDimDefault();
        dragDismiss(DragLayout.DragStyle.Top);
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
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                if (onQuickClickListener != null) {
                    onQuickClickListener.onWanPwd();
                }
            }
        }, R.id.dialog_web_quick_tv_wanpwd);
    }

    public interface OnQuickClickListener {
        void onCopyLink();
        void onBrowser();

        void onWanPwd();
    }
}
