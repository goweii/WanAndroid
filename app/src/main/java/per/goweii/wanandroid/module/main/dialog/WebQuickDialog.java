package per.goweii.wanandroid.module.main.dialog;

import android.view.View;

import androidx.annotation.NonNull;

import per.goweii.anylayer.Layer;
import per.goweii.anylayer.popup.PopupLayer;
import per.goweii.anylayer.widget.SwipeLayout;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/11/9
 * GitHub: https://github.com/goweii
 */
public class WebQuickDialog extends PopupLayer {

    public WebQuickDialog(View targetView, OnQuickClickListener onQuickClickListener) {
        super(targetView);
        setContentView(R.layout.dialog_web_quick);
        setOutsideInterceptTouchEvent(false);
        setInterceptKeyEvent(false);
        setBackgroundDimDefault();
        setSwipeDismiss(SwipeLayout.Direction.TOP);
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                if (onQuickClickListener != null) {
                    onQuickClickListener.onCopyLink();
                }
            }
        }, R.id.dialog_web_quick_tv_copy_link);
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                if (onQuickClickListener != null) {
                    onQuickClickListener.onBrowser();
                }
            }
        }, R.id.dialog_web_quick_tv_browser);
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
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
