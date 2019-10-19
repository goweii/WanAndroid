package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import per.goweii.anylayer.Align;
import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.PopupLayer;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.WebActivity;

/**
 * @author CuiZhen
 * @date 2019/10/19
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CopiedLinkDialog extends PopupLayer implements View.OnTouchListener {

    private final String link;
    private RelativeLayout rl;
    private TextView tvLink;
    private ObjectAnimator restoreAnim;

    public CopiedLinkDialog(View targetView, String link, SimpleListener onClose) {
        super(targetView);
        this.link = link;
        contentView(R.layout.dialog_copied_link);
        interceptKeyEvent(false);
        outsideInterceptTouchEvent(false);
        horizontal(Align.Horizontal.ALIGN_LEFT);
        vertical(Align.Vertical.ABOVE);
        direction(Align.Direction.HORIZONTAL);
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(View target) {
                return AnimatorHelper.createLeftInAnim(target);
            }

            @Override
            public Animator createOutAnimator(View target) {
                return AnimatorHelper.createLeftOutAnim(target);
            }
        });
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                if (onClose != null) {
                    onClose.onResult();
                }
            }
        }, R.id.dialog_copied_link_iv_close);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                if (onClose != null) {
                    onClose.onResult();
                }
                WebActivity.start(getActivity(), link);
            }
        }, R.id.dialog_copied_link_rl);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttach() {
        super.onAttach();
        tvLink = getView(R.id.dialog_copied_link_tv_link);
        rl = getView(R.id.dialog_copied_link_rl);
        tvLink.setText(link);
        rl.setOnTouchListener(this);
    }

    private float touchSlop = ViewConfiguration.getTouchSlop();
    private float tapTimeout = ViewConfiguration.getTapTimeout();
    private float downX = 0;
    private float downY = 0;
    private float moveStartX = 0;
    private float lastTransX = 0;
    private long downTime = 0;
    private boolean dragging = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (restoreAnim != null) {
                    restoreAnim.cancel();
                }
                dragging = false;
                downTime = System.currentTimeMillis();
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                if (dragging) {
                    float tx = lastTransX + moveX - moveStartX;
                    tx = tx > 0 ? 0 : tx;
                    rl.setTranslationX(tx);
                } else if (Math.abs(moveX - downX) >= touchSlop || Math.abs(moveY - downY) >= touchSlop) {
                    // drag
                    dragging = true;
                    moveStartX = moveX;
                    lastTransX = rl.getTranslationX();
                }
                break;
            case MotionEvent.ACTION_UP:
                long upTime = System.currentTimeMillis();
                if (dragging) {
                    dragging = false;
                    if (Math.abs(rl.getTranslationX() - lastTransX) > rl.getWidth() * 0.3F) {
                        dismiss();
                    } else {
                        restore();
                    }
                } else {
                    if (upTime - downTime < tapTimeout) {
                        // tap
                        rl.performClick();
                    }
                }
                break;
        }
        return true;
    }

    private void restore() {
        if (restoreAnim != null) {
            restoreAnim.cancel();
        }
        restoreAnim = ObjectAnimator.ofFloat(rl, "translationX", rl.getTranslationX(), 0F);
        restoreAnim.setInterpolator(new DecelerateInterpolator());
        restoreAnim.start();
    }
}
