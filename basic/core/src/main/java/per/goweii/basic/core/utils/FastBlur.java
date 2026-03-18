package per.goweii.basic.core.utils;

import android.graphics.Bitmap;

import java.util.Objects;

/**
 * 毛玻璃处理
 */
public final class FastBlur {

    private static FastBlur INSTANCE = null;

    private FastBlur() {
    }

    public static FastBlur get() {
        if (INSTANCE == null) {
            synchronized (FastBlur.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FastBlur();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 模糊
     * 采用FastBlur算法
     * 缩小-->模糊
     *
     * @param originalBitmap 原图
     * @param radius         模糊半径
     * @param scale          缩放因子（>=1）
     * @return 模糊Bitmap
     */
    public Bitmap process(final Bitmap originalBitmap,
                          final float radius,
                          final float scale,
                          final boolean keepSize,
                          final boolean recycleOriginal) {
        Objects.requireNonNull(originalBitmap, "待模糊Bitmap不能为空");
        final int newRadius = radius < 0 ? 0 : (int) radius;
        final float newScale = scale <= 0 ? 1 : scale;
        if (newRadius == 0) {
            if (newScale == 1) {
                return originalBitmap;
            }
            Bitmap scaleBitmap = BitmapProcessor.get().scaleBitmap(originalBitmap, newScale);
            if (recycleOriginal) {
                originalBitmap.recycle();
            }
            return scaleBitmap;
        }
        if (newScale == 1) {
            Bitmap output = blur(originalBitmap, newRadius);
            if (recycleOriginal) {
                originalBitmap.recycle();
            }
            return output;
        }
        final int width = originalBitmap.getWidth();
        final int height = originalBitmap.getHeight();
        Bitmap input = BitmapProcessor.get().scaleBitmap(originalBitmap, newScale);
        if (recycleOriginal) {
            originalBitmap.recycle();
        }
        Bitmap output = blur(input, newRadius);
        input.recycle();
        if (!keepSize) {
            return output;
        }
        Bitmap outputScaled = BitmapProcessor.get().scaleBitmap(output, width, height);
        output.recycle();
        return outputScaled;
    }

    public void recycle() {
        INSTANCE = null;
    }

    /**
     * 模糊
     * 直接模糊原图
     *
     * @param originalBitmap 原图
     * @param radius         模糊半径
     * @return 模糊Bitmap
     */
    private static Bitmap blur(Bitmap originalBitmap, int radius) {

        if (radius <= 0) {
            return originalBitmap;
        }

        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
