package per.goweii.basic.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 马赛克
 *
 * @author Cuizhen
 * @date 2018/6/27-下午2:00
 */
public class MosaicUtils {

    /**
     * 马赛克
     *
     * @param bitmap    普通图像
     * @param zoneCount 像素图的大像素方块在短边的个数
     * @return Bitmap
     */
    public static Bitmap mosaicWithCount(Bitmap bitmap, int zoneCount) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int min = Math.min(bitmapWidth, bitmapHeight);
        if (zoneCount >= min) {
            return bitmap;
        }
        return mosaic(bitmap, min / zoneCount);
    }

    /**
     * 马赛克
     *
     * @param bitmap
     * @param zoneWidth 像素图的大像素的宽度
     * @return
     */
    public static Bitmap mosaic(Bitmap bitmap, int zoneWidth) {
        return mosaic(bitmap, zoneWidth, 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 马赛克
     *
     * @param bitmap   原图
     * @param zoneSize 方块大小
     * @param startX   马赛克区域起点x
     * @param startY   马赛克区域起点y
     * @param endX     马赛克区域宽
     * @param endY     马赛克区域高
     * @return 马赛图
     */
    public static Bitmap mosaic(Bitmap bitmap, int zoneSize, int startX, int startY, int endX, int endY) {
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int x1 = startX < 0 ? 0 : startX > w ? w : startX;
        final int y1 = startY < 0 ? 0 : startY > h ? h : startY;
        final int x2 = endX < 0 ? 0 : endX > w ? w : endX;
        final int y2 = endY < 0 ? 0 : endY > h ? h : endY;
        final int l = x1 < x2 ? x1 : x2;
        final int t = y1 < y2 ? y1 : y2;
        final int r = x1 < x2 ? x2 : x1;
        final int b = y1 < y2 ? y2 : y1;
        final int s = zoneSize;
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);
        Paint p = new Paint();
        for (int i_ = l, _i; i_ < r; i_ += s) {
            for (int j_ = t, _j; j_ < b; j_ += s) {
                _i = i_ + s > r ? r : i_ + s;
                _j = j_ + s > b ? b : j_ + s;
                p.setColor(bitmap.getPixel((i_ + _i) / 2, (j_ + _j) / 2));
                c.drawRect(i_, j_, _i, _j, p);
            }
        }
        return result;
    }

}
