package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.CopiedTextProcessor;
import per.goweii.wanandroid.utils.wanpwd.WanPwdParser;

/**
 * @author CuiZhen
 * @date 2019/10/19
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class PasswordDialog extends DialogLayer {

    private final WanPwdParser mParser;

    private Handler mHandler;
    private ObjectAnimator mAnim;

    public PasswordDialog(Context context, WanPwdParser parser) {
        super(context);
        this.mParser = parser;
        contentView(R.layout.dialog_password);
        backgroundDimAmount(0.5F);
        cancelableOnClickKeyBack(false);
        cancelableOnTouchOutside(false);
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(View target) {
                Animator a = AnimatorHelper.createZoomAlphaInAnim(target);
                a.setInterpolator(new OvershootInterpolator());
                return a;
            }

            @Override
            public Animator createOutAnimator(View target) {
                Animator a = AnimatorHelper.createZoomAlphaOutAnim(target);
                a.setInterpolator(new OvershootInterpolator());
                return a;
            }
        });
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                CopiedTextProcessor.getInstance().processed();
            }
        }, R.id.dialog_password_iv_close);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                CopiedTextProcessor.getInstance().processed();
                Runnable runnable = mParser.getWanPwd().getRunnable();
                if (runnable != null) runnable.run();
            }
        }, R.id.dialog_password_tv_open);
    }

    public WanPwdParser getPassword() {
        return mParser;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttach() {
        super.onAttach();
        mHandler = new Handler();
        TextView tvContent = getView(R.id.dialog_password_tv_content);
        TextView tvOpen = getView(R.id.dialog_password_tv_open);
        tvContent.setText(mParser.getWanPwd().getShowText());
        tvOpen.setText(mParser.getWanPwd().getBtnText());
    }

    @Override
    public void onShow() {
        super.onShow();
        doEyeAnim();
    }

    @Override
    public void onPreRemove() {
        super.onPreRemove();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        if (mAnim != null) {
            mAnim.cancel();
            mAnim = null;
        }
    }

    private void doEyeAnim() {
        View flEye = getView(R.id.dialog_password_fl_eye);
        if (flEye == null) {
            return;
        }
        if (mAnim == null) {
            int h = flEye.getHeight();
            if (h <= 0) return;
            mAnim = ObjectAnimator.ofFloat(flEye, "translationY", 0, h, 0);
            mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnim.setDuration(350);
            mAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doEyeAnim();
                            }
                        }, (long) (Math.random() * 5000 + 5000));
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        mAnim.setRepeatCount((int) (Math.random() + 0.5));
        mAnim.start();
    }
}
