package per.goweii.wanandroid.module.main.dialog;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import per.goweii.anylayer.Layer;
import per.goweii.anylayer.popup.PopupLayer;
import per.goweii.anylayer.widget.SwipeLayout;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.CopiedTextProcessor;
import per.goweii.wanandroid.utils.router.Router;

/**
 * @author CuiZhen
 * @date 2019/10/19
 * GitHub: https://github.com/goweii
 */
public class CopiedLinkDialog extends PopupLayer {

    private final String link;

    public CopiedLinkDialog(View targetView, String link) {
        super(targetView);
        this.link = link;
        contentView(R.layout.dialog_copied_link);
        interceptKeyEvent(false);
        outsideInterceptTouchEvent(false);
        horizontal(Align.Horizontal.ALIGN_LEFT);
        vertical(Align.Vertical.ALIGN_BOTTOM);
        direction(Align.Direction.HORIZONTAL);
        swipeDismiss(SwipeLayout.Direction.LEFT);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                CopiedTextProcessor.getInstance().processed();
            }
        }, R.id.dialog_copied_link_iv_close);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                CopiedTextProcessor.getInstance().processed();
                Router.routeTo(link);
            }
        }, R.id.dialog_copied_link_rl);
    }

    public String getLink() {
        return link;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        TextView tvLink = getView(R.id.dialog_copied_link_tv_link);
        tvLink.setText(link);
    }
}
