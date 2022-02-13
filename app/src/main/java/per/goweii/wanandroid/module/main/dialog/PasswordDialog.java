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

import androidx.annotation.NonNull;

import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.anylayer.utils.AnimatorHelper;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.CopiedTextProcessor;
import per.goweii.wanandroid.utils.wanpwd.WanPwdParser;

/**
 * @author CuiZhen
 * @date 2019/10/19
 * GitHub: https://github.com/goweii
 */
public class PasswordDialog extends DialogLayer {

    private final WanPwdParser mParser;

    private Handler mHandler;
    private ObjectAnimator mAnim;

    public PasswordDialog(Context context, WanPwdParser parser) {
        super(context);
        this.mParser = parser;
        setContentView(R.layout.dialog_password);
        setBackgroundDimAmount(0.5F);
        setCancelableOnClickKeyBack(false);
        setCancelableOnTouchOutside(false);
        setContentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(@NonNull View target) {
                Animator a = AnimatorHelper.createZoomAlphaInAnim(target);
                a.setInterpolator(new OvershootInterpolator());
                return a;
            }

            @Override
            public Animator createOutAnimator(@NonNull View target) {
                Animator a = AnimatorHelper.createZoomAlphaOutAnim(target);
                a.setInterpolator(new OvershootInterpolator());
                return a;
            }
        });
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                CopiedTextProcessor.getInstance().processed();
            }
        }, R.id.dialog_password_iv_close);
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
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
        TextView tvContent = requireView(R.id.dialog_password_tv_content);
        TextView tvOpen = requireView(R.id.dialog_password_tv_open);
        tvContent.setText(mParser.getWanPwd().getShowText());
        tvOpen.setText(mParser.getWanPwd().getBtnText());
    }

    @Override
    protected void onPostShow() {
        super.onPostShow();
        doEyeAnim();
    }

    @Override
    protected void onPreDismiss() {
        super.onPreDismiss();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        if (mAnim != null) {
            mAnim.cancel();
            mAnim = null;
        }
    }

    private void doEyeAnim() {
        View flEye = findView(R.id.dialog_password_fl_eye);
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
