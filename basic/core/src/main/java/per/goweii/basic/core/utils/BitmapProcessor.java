package per.goweii.basic.core.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

/**
 * @author CuiZhen
 */
class BitmapProcessor {
    private static BitmapProcessor INSTANCE = null;

    private boolean mRealTimeMode = false;

    private PaintFlagsDrawFilter mFilter = null;
    private Paint mPaint = null;
    private Canvas mCanvas = null;

    static BitmapProcessor get() {
        if (INSTANCE == null) {
            synchronized (BitmapProcessor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BitmapProcessor();
                }
            }
        }
        return INSTANCE;
    }

    private BitmapProcessor() {
    }

    void realTimeMode(boolean realTimeMode) {
        mRealTimeMode = realTimeMode;
        if (mRealTimeMode) {
            prepare();
        } else {
            recycle();
        }
    }

    private void prepare() {
        if (mFilter == null) {
            mFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }
        if (mPaint == null) {
            mPaint = new Paint();
        }
        if (mCanvas == null) {
            mCanvas = new Canvas();
        }
    }

    private void recycle() {
        mFilter = null;
        mPaint = null;
        mCanvas = null;
    }

    private Canvas reuseOrCreateCanvas() {
        final Canvas canvas;
        if (mRealTimeMode) {
            prepare();
            canvas = mCanvas;
        } else {
            canvas = new Canvas();
        }
        return canvas;
    }

    private PaintFlagsDrawFilter reuseOrCreateFilter() {
        final PaintFlagsDrawFilter filter;
        if (mRealTimeMode) {
            prepare();
            filter = mFilter;
        } else {
            filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }
        return filter;
    }

    private Paint reuseOrCreatePaint() {
        final Paint paint;
        if (mRealTimeMode) {
            prepare();
            paint = mPaint;
        } else {
            paint = new Paint();
        }
        return paint;
    }

    Bitmap snapshot(View from, int bgColor, int fgColor, float scale, boolean antiAlias) {
        final float newScale = scale > 0 ? scale : 1;
        final int w = (int) (from.getWidth() * newScale);
        final int h = (int) (from.getHeight() * newScale);
        Bitmap output = Bitmap.createBitmap(w <= 0 ? 1 : w, h <= 0 ? 1 : h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = reuseOrCreateCanvas();
        canvas.setBitmap(output);
        if (antiAlias) {
            canvas.setDrawFilter(reuseOrCreateFilter());
        } else {
            canvas.setDrawFilter(null);
        }
        canvas.save();
        canvas.scale(newScale, newScale);
        if (bgColor != 0) {
            canvas.drawColor(bgColor);
        }
        from.draw(canvas);
        if (fgColor != 0) {
            canvas.drawColor(fgColor);
        }
        canvas.restore();
        return output;
    }

    Bitmap clip(Bitmap bitmap, View from, ImageView into, boolean fitXY, boolean antiAlias) {
        int[] lf = new int[2];
        from.getLocationOnScreen(lf);
        int[] lt = new int[2];
        into.getLocationOnScreen(lt);
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        float sx = (float) bw / (float) from.getWidth();
        float sh = (float) bh / (float) from.getHeight();
        Rect rf = new Rect(
                (int) ((lt[0] - lf[0]) * sx),
                (int) ((lt[1] - lf[1]) * sh),
                (int) ((lt[0] - lf[0]) * sx + into.getWidth() * sx),
                (int) ((lt[1] - lf[1]) * sh + into.getHeight() * sh)
        );
        Rect rt = new Rect(0, 0, into.getWidth(), into.getHeight());
        if (!fitXY) {
            float s = Math.max(
                    (float) into.getWidth() / (float) rf.width(),
                    (float) into.getHeight() / (float) rf.height()
            );
            if (s > 1) {
                rt.right = rf.width();
                rt.bottom = rf.height();
            }
        }
        Bitmap output = Bitmap.createBitmap(rt.width(), rt.height(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = reuseOrCreateCanvas();
        canvas.setBitmap(output);
        if (antiAlias) {
            canvas.setDrawFilter(reuseOrCreateFilter());
        } else {
            canvas.setDrawFilter(null);
        }
        Paint paint = null;
        if (antiAlias) {
            paint = reuseOrCreatePaint();
            paint.setXfermode(null);
            paint.setAntiAlias(true);
        }
        canvas.drawBitmap(bitmap, rf, rt, paint);
        return output;
    }

    private static class StopDrawException extends Exception {
    }

    Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        return scaleBitmap(bitmap, scale, true);
    }

    Bitmap scaleBitmap(Bitmap bitmap, float scale, boolean antiAlias) {
        final int iw = bitmap.getWidth();
        final int ih = bitmap.getHeight();
        final int ow = (int) (iw * scale);
        final int oh = (int) (ih * scale);
        return scaleBitmap(bitmap, ow, oh, antiAlias);
    }

    Bitmap scaleBitmap(Bitmap bitmap, int w, int h) {
        return scaleBitmap(bitmap, w, h, true);
    }

    Bitmap scaleBitmap(Bitmap bitmap, int w, int h, boolean antiAlias) {
        final Bitmap.Config config = bitmap.getConfig();
        final Bitmap.Config newConfig;
        switch (config) {
            case RGB_565:
                newConfig = Bitmap.Config.RGB_565;
                break;
            case ALPHA_8:
                newConfig = Bitmap.Config.ALPHA_8;
                break;
            case ARGB_4444:
            case ARGB_8888:
            default:
                newConfig = Bitmap.Config.ARGB_8888;
                break;
        }
        Bitmap output = Bitmap.createBitmap(w, h, newConfig);
        final Canvas canvas = reuseOrCreateCanvas();
        canvas.setBitmap(output);
        if (antiAlias) {
            canvas.setDrawFilter(reuseOrCreateFilter());
        } else {
            canvas.setDrawFilter(null);
        }
        Paint paint = null;
        if (antiAlias) {
            paint = reuseOrCreatePaint();
            paint.setXfermode(null);
            paint.setAntiAlias(true);
        }
        canvas.drawBitmap(bitmap,
                new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(0, 0, w, h),
                paint);
        return output;
    }
}
