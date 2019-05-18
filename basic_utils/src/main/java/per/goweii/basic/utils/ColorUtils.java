package per.goweii.basic.utils;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/9/6
 */
public class ColorUtils {

    /**
     * 计算颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int alphaColor(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        float a = alpha / 255f;
        return alphaColor(color, a);
    }

    /**
     * 计算颜色
     *
     * @param color color值
     * @param alpha alpha值[0-1]
     * @return 最终的状态栏颜色
     */
    public static int alphaColor(@ColorInt int color, @FloatRange(from = 0, to = 1) float alpha) {
        if (alpha == 1) {
            return color;
        }
        if (alpha == 0){
            return 0;
        }
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * alpha + 0.5);
        green = (int) (green * alpha + 0.5);
        blue = (int) (blue * alpha + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 根据fraction值来计算当前的颜色
     *
     * @param colorFrom 起始颜色
     * @param colorTo   结束颜色
     * @param fraction  变量
     * @return 当前颜色
     */
    public static int changingColor(@ColorInt int colorFrom, @ColorInt int colorTo, @FloatRange(from = 0, to = 1) float fraction) {
        int redStart = Color.red(colorFrom);
        int blueStart = Color.blue(colorFrom);
        int greenStart = Color.green(colorFrom);
        int alphaStart = Color.alpha(colorFrom);

        int redEnd = Color.red(colorTo);
        int blueEnd = Color.blue(colorTo);
        int greenEnd = Color.green(colorTo);
        int alphaEnd = Color.alpha(colorTo);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }

}
