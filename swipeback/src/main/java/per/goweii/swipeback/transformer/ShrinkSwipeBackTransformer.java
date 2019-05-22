package per.goweii.swipeback.transformer;

import android.support.annotation.FloatRange;
import android.view.View;

import per.goweii.swipeback.SwipeBackLayout;

/**
 * @author CuiZhen
 * @date 2019/5/22
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ShrinkSwipeBackTransformer implements SwipeBackLayout.SwipeBackTransformer {

    private final float mScale;
    private final float mAlpha;

    public ShrinkSwipeBackTransformer() {
        this(0.96F, 1F);
    }

    public ShrinkSwipeBackTransformer(@FloatRange(from = 0, to = 1) float scale,
                                      @FloatRange(from = 0, to = 1) float alpha) {
        mScale = scale;
        mAlpha = alpha;
    }

    @Override
    public void transform(View currentView, View previousView, float fraction, int swipeDirection) {
        float scale = mScale + (1 - mScale) * fraction;
        previousView.setScaleX(scale);
        previousView.setScaleY(scale);
        float alpha = mAlpha + (1 - mAlpha) * fraction;
        previousView.setAlpha(alpha);
    }
}
