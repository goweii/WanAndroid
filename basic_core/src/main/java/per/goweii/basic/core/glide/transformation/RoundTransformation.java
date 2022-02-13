package per.goweii.basic.core.glide.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class RoundTransformation extends BitmapTransformation {
    private static final String ID = BlurTransformation.class.getName();

    private float radius = 0f;

    public RoundTransformation(@Px int radius) {
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(@NonNull BitmapPool pool, Bitmap source) {
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "RoundTransformation(radius=" + radius + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RoundTransformation && ((RoundTransformation) o).radius == radius;
    }

    @Override
    public int hashCode() {
        return (ID.hashCode() + Float.valueOf(radius).hashCode());
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius).getBytes(CHARSET));
    }
}