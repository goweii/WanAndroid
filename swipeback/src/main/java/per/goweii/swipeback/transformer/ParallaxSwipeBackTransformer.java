package per.goweii.swipeback.transformer;

import android.support.annotation.FloatRange;
import android.view.View;

import per.goweii.swipeback.SwipeBackLayout;
import per.goweii.swipeback.SwipeDirection;

/**
 * @author CuiZhen
 * @date 2019/5/22
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ParallaxSwipeBackTransformer implements SwipeBackLayout.SwipeBackTransformer {

    private final float mPercent;
    private final float mAlpha;

    private int mMaxTranslation = -1;

    public ParallaxSwipeBackTransformer() {
        this(0.12F, 1F);
    }

    public ParallaxSwipeBackTransformer(@FloatRange(from = 0, to = 1) float percent,
                                        @FloatRange(from = 0, to = 1) float alpha) {
        mPercent = percent;
        mAlpha = alpha;
    }

    @Override
    public void transform(View currentView, View previousView, float fraction, int swipeDirection) {
        if (swipeDirection == SwipeDirection.FROM_LEFT) {
            if (mMaxTranslation == -1) {
                mMaxTranslation = previousView.getWidth();
            }
            float translation = (mMaxTranslation * mPercent) * (1 - fraction);
            previousView.setTranslationX(-translation);
        } else if (swipeDirection == SwipeDirection.FROM_RIGHT) {
            if (mMaxTranslation == -1) {
                mMaxTranslation = previousView.getWidth();
            }
            float translation = mMaxTranslation * mPercent * (1 - fraction);
            previousView.setTranslationX(translation);
        } else if (swipeDirection == SwipeDirection.FROM_TOP) {
            if (mMaxTranslation == -1) {
                mMaxTranslation = previousView.getHeight();
            }
            float translation = mMaxTranslation * mPercent * (1 - fraction);
            previousView.setTranslationY(-translation);
        } else if (swipeDirection == SwipeDirection.FROM_BOTTOM) {
            if (mMaxTranslation == -1) {
                mMaxTranslation = previousView.getHeight();
            }
            float translation = mMaxTranslation * mPercent * (1 - fraction);
            previousView.setTranslationY(translation);
        }
        float alpha = mAlpha + (1 - mAlpha) * fraction;
        previousView.setAlpha(alpha);
    }
}
