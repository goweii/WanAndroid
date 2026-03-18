package per.goweii.basic.core.glide.transformation;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

import per.goweii.basic.core.utils.FastBlur;

/**
 * 描述：高斯模糊
 *
 * @author Cuizhen
 * @date 2018/9/13
 */
public class BlurTransformation extends BitmapTransformation {

    private static final String ID = BlurTransformation.class.getName();

    private static float PERCENT = 0.1F;

    private float percent;

    public BlurTransformation() {
        this(PERCENT);
    }

    public BlurTransformation(float percent) {
        this.percent = percent;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (percent <= 0) {
            return toTransform;
        }
        float radius = Math.min(toTransform.getWidth(), toTransform.getHeight()) * percent;
        float scale = 1;
        if (radius > 25) {
            scale = scale / (radius / 25);
            radius = 25;
        }
        return FastBlur.get().process(toTransform, radius, scale, false, false);
    }

    @NonNull
    @Override
    public String toString() {
        return "BlurTransformation(percent=" + percent + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlurTransformation && ((BlurTransformation) o).percent == percent;
    }

    @Override
    public int hashCode() {
        return (ID.hashCode() + Float.valueOf(percent).hashCode());
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + percent).getBytes(CHARSET));
    }
}
