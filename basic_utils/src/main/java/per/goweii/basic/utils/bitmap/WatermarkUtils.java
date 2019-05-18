package per.goweii.basic.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;

/**
 * 水印
 *
 * @author Cuizhen
 * @date 2018/6/27-下午2:33
 */
public class WatermarkUtils {

    private static final float MAX_X_PERCENT = 0.3F;
    private static final float MAX_Y_PERCENT = 0.1F;

    /**
     * 图片添加水印
     *
     * @param original 原图
     * @param mark     水印
     * @return Bitmap
     */
    public static Bitmap mark(Bitmap original, Bitmap mark, int x, int y) {
        if (original == null) {
            return null;
        }
        Bitmap result = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        canvas.drawBitmap(original, 0, 0, paint);
        canvas.drawBitmap(mark, x, y, paint);
        return result;
    }

    /**
     * 图片添加水印文字
     *
     * @param original  原图
     * @param textStr   文字
     * @param textSize  字体大小
     * @param textColor 字体颜色
     * @param alpha     透明度
     * @return Bitmap
     */
    public static Bitmap mark(Bitmap original,
                              String textStr, int textSize, int textColor,
                              int alpha,
                              float xOffsetPercent, float yOffsetPercent,
                              boolean inside) {
        Bitmap result = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(original, 0, 0, paint);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setAlpha(alpha);

        Rect rect = new Rect();
        paint.getTextBounds(textStr, 0, textStr.length(), rect);

        int x, y;
        int w = original.getWidth();
        int h = original.getHeight();
        if (inside) {
            x = (int) ((w - rect.right) * xOffsetPercent);
            y = (int) ((h - rect.bottom) * yOffsetPercent);
        } else {
            x = (int) (w * xOffsetPercent);
            y = (int) (h * yOffsetPercent);
        }

        canvas.drawText(textStr, x, y, paint);
        return result;
    }
}