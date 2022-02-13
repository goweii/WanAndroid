package per.goweii.basic.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Random;

/**
 * 动画帮助类
 *
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/30-上午10:51
 */
public class AnimatorUtils {

    /**
     * 改变一个View的宽度和高度
     *
     * @param target       目标View
     * @param toWidth      目标宽度
     * @param toHeight     目标高度
     * @param duration     时长
     * @param interpolator 时间插值器默认为DecelerateInterpolator
     */
    public static void changeLargeViewSize(final View target, int toWidth, int toHeight, long duration, Interpolator interpolator) {
        final ViewGroup.LayoutParams params = target.getLayoutParams();
        final float toX = (float) toWidth / (float) params.width;
        final float toY = (float) toHeight / (float) params.height;

        ScaleAnimation scaleAnimation = new ScaleAnimation(1, toX, 1, toY, 0, 0);
        scaleAnimation.setDuration(duration);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(interpolator != null ? interpolator : new DecelerateInterpolator());
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1, 1, 1, 0, 0);
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                target.startAnimation(scaleAnimation);
                params.width = toWidth;
                params.height = toHeight;
                target.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        target.startAnimation(scaleAnimation);
    }

    /**
     * 改变一个View的宽度和高度
     *
     * @param target       目标View
     * @param toWidth      目标宽度
     * @param toHeight     目标高度
     * @param duration     时长
     * @param interpolator 时间插值器默认为DecelerateInterpolator
     */
    public static void changeViewSize(final View target, int toWidth, int toHeight, long duration, TimeInterpolator interpolator) {
        final ViewGroup.LayoutParams params = target.getLayoutParams();
        final int fromWidth = params.width;
        final int fromHeight = params.height;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator != null ? interpolator : new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float f = (float) animator.getAnimatedValue();
                float nowWidth = fromWidth + (toWidth - fromWidth) * f;
                float nowHeight = fromHeight + (toHeight - fromHeight) * f;
                params.width = (int) nowWidth;
                params.height = (int) nowHeight;
                target.requestLayout();
            }
        });
        valueAnimator.start();
    }

    /**
     * 改变一个View的宽度
     *
     * @param view     需要该表的View
     * @param width    宽度
     * @param duration 时长
     */
    public static void changeViewWidth(final View view, int width, long duration, TimeInterpolator interpolator) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(view.getLayoutParams().width, width);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator != null ? interpolator : new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.getLayoutParams().width = (int) animator.getAnimatedValue();
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }

    /**
     * 改变一个View的高度
     *
     * @param view     需要该表的View
     * @param height   高度
     * @param duration 时长
     */
    public static void changeViewHeight(final View view, int height, long duration, TimeInterpolator interpolator) {
        int lastH = view.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(lastH, height);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator != null ? interpolator : new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.getLayoutParams().height = (int) animator.getAnimatedValue();
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }

    /**
     * 改变一个View的背景色
     *
     * @param view      View
     * @param colorFrom
     * @param colorTo   结束颜色
     * @param duration  时长
     */
    public static void changeViewBgColor(final View view, final int colorFrom, final int colorTo, long duration, TimeInterpolator interpolator) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator != null ? interpolator : new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float f = (float) animator.getAnimatedValue();
                view.setBackgroundColor(ColorUtils.changingColor(colorFrom, colorTo, f));
            }
        });
        valueAnimator.start();
    }

    /**
     * 旋转的动画
     *
     * @param target      需要选择的View
     * @param fromDegrees 初始的角度【从这个角度开始】
     * @param toDegrees   当前需要旋转的角度【转到这个角度来】
     */
    public static void rotateCenter(View target, float fromDegrees, float toDegrees, long duration) {
        float centerX = target.getWidth() / 2.0f;
        float centerY = target.getHeight() / 2.0f;
        //这个是设置需要旋转的角度（也是初始化），我设置的是当前需要旋转的角度
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, centerX, centerY);
        //这个是设置动画时间的
        rotateAnimation.setDuration(duration);
        //动画执行完毕后是否停在结束时的角度上
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        //启动动画
        target.startAnimation(rotateAnimation);
    }

    public static void translationX(final View view, int start, int end, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void translationY(final View view, int start, int end, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void doBigDecimalAnim(final TextView target, BigDecimal from, BigDecimal to, final int scaleCount, long duration) {
        ValueAnimator animator = ValueAnimator.ofObject(new TypeEvaluator<BigDecimal>() {
            @Override
            public BigDecimal evaluate(float fraction, BigDecimal startValue, BigDecimal endValue) {
                return startValue.add(endValue.subtract(startValue).multiply(new BigDecimal(fraction)));
            }
        }, from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                BigDecimal value = (BigDecimal) animation.getAnimatedValue();
                if (target != null) {
                    target.setText(value.setScale(scaleCount, BigDecimal.ROUND_DOWN).toString());
                }
            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void doBigDecimalAnim(final TextView target, BigDecimal to, final int scaleCount, long duration) {
        BigDecimal from;
        try {
            from = new BigDecimal(target.getText().toString());
        } catch (NumberFormatException e) {
            from = new BigDecimal(0);
        }
        doBigDecimalAnim(target, from, to, scaleCount, duration);
    }

    public static void doBigDecimalAnim(final TextView target, String to, final int scaleCount, long duration) {
        BigDecimal tob;
        try {
            tob = new BigDecimal(target.getText().toString());
        } catch (NumberFormatException e) {
            tob = new BigDecimal(0);
        }
        doBigDecimalAnim(target, tob, scaleCount, duration);
    }

    public static void doFloatAnim(final TextView target, float from, float to, int scaleCount, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        final String format = "%." + scaleCount + "f";
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (target != null) {
                    target.setText(String.format(format, value));
                }
            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void doFloatAnim(final TextView target, float to, int scaleCount, long duration) {
        float from;
        try {
            from = Float.valueOf(target.getText().toString());
        } catch (NumberFormatException e) {
            from = 0F;
        }
        doFloatAnim(target, from, to, scaleCount, duration);
    }

    public static void doFloatAnim(final TextView target, String to, int scaleCount, long duration) {
        float tof;
        try {
            tof = Float.valueOf(to);
        } catch (NumberFormatException e) {
            tof = 0F;
        }
        doFloatAnim(target, tof, scaleCount, duration);
    }

    public static void doIntAnim(final TextView target, int from, int to, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (target != null) {
                    target.setText(String.format("%d", value));
                }
            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void doIntAnim(final TextView target, int to, long duration) {
        String fromStr = target.getText().toString();
        int from;
        try {
            from = Integer.parseInt(fromStr);
        } catch (NumberFormatException e) {
            from = 0;
        }
        doIntAnim(target, from, to, duration);
    }

    public static void doIntAnim(final TextView target, String to, long duration) {
        int toi;
        try {
            toi = Integer.valueOf(target.getText().toString());
        } catch (NumberFormatException e) {
            toi = 0;
        }
        doIntAnim(target, toi, duration);
    }

    public static void doStringAnim(final TextView target, String from, String to, long duration) {
        if (from == null || to == null) {
            return;
        }
        if (TextUtils.equals(from, to)) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofObject(new TypeEvaluator<String>() {
            private int startLength = 0;
            private int endLength = 0;
            private StringBuilder baseBuilder = new StringBuilder();
            private Random random = new Random();

            @Override
            public String evaluate(float fraction, String startValue, String endValue) {
                if (fraction == 0) {
                    startLength = startValue.length();
                    endLength = endValue.length();
                    char[] chars = (startValue + endValue).toCharArray();
                    for (char c : chars) {
                        if (baseBuilder.indexOf(String.valueOf(c)) >= 0) {
                            continue;
                        }
                        if (c >= '0' && c <= '9') {
                            baseBuilder.append("0123456789");
                        } else if (c >= 'a' && c <= 'z') {
                            baseBuilder.append("abcdefghijklmnopqrstuvwxyz");
                        } else if (c >= 'A' && c <= 'Z') {
                            baseBuilder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                        } else {
                            baseBuilder.append(c);
                        }
                    }
                }
                if (baseBuilder.length() == 0) {
                    return endValue;
                }
                if (fraction == 1) {
                    return endValue;
                }
                int length = (int) (startLength + (endLength - startLength) * fraction);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    int number = random.nextInt(baseBuilder.length());
                    stringBuilder.append(baseBuilder.charAt(number));
                }
                return stringBuilder.toString();
            }
        }, from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String value = (String) animation.getAnimatedValue();
                if (target != null) {
                    target.setText(value);
                }
            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void flipHorizontal(final View target, final View oldView, final View newView, final long time, final boolean downUp) {
        Object tag = target.getTag();
        if (tag instanceof String && TextUtils.equals("flipHorizontal", (String) tag)) {
            return;
        }
        float from1, to1, from2, to2;
        if (downUp) {
            from1 = target.getRotationX();
            to1 = 90F;
            from2 = -90F;
            to2 = 0F;
        } else {
            from1 = target.getRotationX();
            to1 = -90F;
            from2 = 90F;
            to2 = 0F;
        }
        final ObjectAnimator animator1 = ObjectAnimator.ofFloat(target, "rotationX", from1, to1).setDuration(time);
        animator1.setInterpolator(new AccelerateInterpolator(2.0f));
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(target, "rotationX", from2, to2).setDuration(time);
        animator2.setInterpolator(new OvershootInterpolator(2.0f));
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                newView.setVisibility(View.VISIBLE);
                animator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        target.setTag("flipHorizontal");
        animator1.start();
    }

    public static void flipVertical(final View target, final View oldView, final View newView, final long time, final float cameraDistance, final boolean leftRight) {
        Object tag = target.getTag();
        if (tag instanceof String && TextUtils.equals("flipVertical", (String) tag)) {
            return;
        }
        float from1, to1, from2, to2;
        if (leftRight) {
            from1 = target.getRotationY();
            to1 = 90F;
            from2 = -90F;
            to2 = 0F;
        } else {
            from1 = target.getRotationY();
            to1 = -90F;
            from2 = 90F;
            to2 = 0F;
        }
        float scale = target.getContext().getResources().getDisplayMetrics().density * cameraDistance;
        target.setCameraDistance(scale);
        final ObjectAnimator animator1 = ObjectAnimator.ofFloat(target, "rotationY", from1, to1).setDuration(time);
        animator1.setInterpolator(new AccelerateInterpolator(2.0f));
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(target, "rotationY", from2, to2).setDuration(time);
        animator2.setInterpolator(new OvershootInterpolator(2.0f));
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                target.setTag(null);
                oldView.setVisibility(View.INVISIBLE);
                newView.setVisibility(View.VISIBLE);
                animator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        target.setTag("flipVertical");
        animator1.start();
    }
}
