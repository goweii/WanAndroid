package per.goweii.basic.core.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import per.goweii.basic.core.glide.progress.OnProgressListener;
import per.goweii.basic.core.glide.progress.ProgressInterceptor;
import per.goweii.basic.utils.Utils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;

/**
 * Glide图片加载框架的帮助类
 *
 * @author Cuizhen
 * @date 2018/8/29-上午9:51
 */
public class GlideHelper {

    private final RequestManager mManager;
    private RequestBuilder<Drawable> mBuilder;
    private RequestBuilder<Bitmap> mBuilderBmp;
    private RequestBuilder<GifDrawable> mBuilderGif;
    private boolean mCache = true;
    private int mPlaceHolder = 0;
    private int mErrorHolder = 0;
    private OnGlideProgressListener mOnGlideProgressListener = null;
    private OnProgressListener mProgressListener = null;
    private Handler mProgressHandler = null;
    private String mImageUrl;
    private BitmapTransformation mBitmapTransformation = null;

    private GlideHelper(Context context) {
        mManager = Glide.with(checkContext(context));
    }

    public static GlideHelper with(Context context) {
        return new GlideHelper(context);
    }

    public void pauseRequests() {
        mManager.pauseRequests();
    }

    public void resumeRequests() {
        mManager.resumeRequests();
    }

    public GlideHelper highQuality() {
        mManager.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
        return this;
    }

    public GlideHelper cache(boolean cache) {
        this.mCache = cache;
        return this;
    }

    public GlideHelper placeHolder(@DrawableRes int placeHolder) {
        this.mPlaceHolder = placeHolder;
        return this;
    }

    public GlideHelper errorHolder(@DrawableRes int errorHolder) {
        this.mErrorHolder = errorHolder;
        return this;
    }

    public GlideHelper transformation(BitmapTransformation transformation) {
        this.mBitmapTransformation = transformation;
        return this;
    }

    @SuppressLint("HandlerLeak")
    public GlideHelper onProgressListener(OnGlideProgressListener listener) {
        mOnGlideProgressListener = listener;
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mOnGlideProgressListener != null) {
                    float progress = (float) msg.obj;
                    mOnGlideProgressListener.onProgress(progress);
                }
            }
        };
        return this;
    }

    public enum As {
        DRAWABLE, GIF, BITMAP
    }

    private As as = As.DRAWABLE;

    public GlideHelper asDrawable() {
        as = As.DRAWABLE;
        return this;
    }

    public GlideHelper asGif() {
        as = As.GIF;
        return this;
    }

    public GlideHelper asBitmap() {
        as = As.BITMAP;
        return this;
    }

    public GlideHelper load(String url) {
        this.mImageUrl = url;
        switch (as) {
            case DRAWABLE:
                mBuilder = getBuilder().load(url);
                break;
            case GIF:
                mBuilderGif = getGifBuilder().load(url);
                break;
            case BITMAP:
                mBuilderBmp = getBmpBuilder().load(url);
                break;
        }
        return this;
    }

    public GlideHelper load(Uri uri) {
        switch (as) {
            case DRAWABLE:
                mBuilder = getBuilder().load(uri);
                break;
            case GIF:
                mBuilderGif = getGifBuilder().load(uri);
                break;
            case BITMAP:
                mBuilderBmp = getBmpBuilder().load(uri);
                break;
        }
        return this;
    }

    public GlideHelper load(int resId) {
        switch (as) {
            case DRAWABLE:
                mBuilder = getBuilder().load(resId);
                break;
            case GIF:
                mBuilderGif = getGifBuilder().load(resId);
                break;
            case BITMAP:
                mBuilderBmp = getBmpBuilder().load(resId);
                break;
        }
        return this;
    }

    public GlideHelper load(Bitmap bitmap) {
        switch (as) {
            case DRAWABLE:
                mBuilder = getBuilder().load(bitmap);
                break;
            case GIF:
                mBuilderGif = getGifBuilder().load(bitmap);
                break;
            case BITMAP:
                mBuilderBmp = getBmpBuilder().load(bitmap);
                break;
        }
        return this;
    }

    public void into(ImageView imageView) {
        if (mOnGlideProgressListener != null && mImageUrl != null) {
            mProgressListener = new OnProgressListener() {
                @Override
                public void onProgress(float progress) {
                    if (mProgressHandler != null) {
                        Message msg = mProgressHandler.obtainMessage();
                        msg.obj = progress;
                        mProgressHandler.sendMessage(msg);
                    }
                }
            };
        }
        switch (as) {
            case DRAWABLE:
                getBuilder().apply(getOptions()).into(new ImageViewTarget<Drawable>(imageView) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        notifyLoadStarted();
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        notifyResourceReady();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        notifyLoadFailed();
                    }
                });
                break;
            case GIF:
                getGifBuilder().apply(getOptions()).into(new ImageViewTarget<GifDrawable>(imageView) {
                    @Override
                    protected void setResource(@Nullable GifDrawable resource) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        notifyLoadStarted();
                    }

                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        super.onResourceReady(resource, transition);
                        notifyResourceReady();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        notifyLoadFailed();
                    }
                });
                break;
            case BITMAP:
                getBmpBuilder().apply(getOptions()).into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        notifyLoadStarted();
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        notifyResourceReady();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        notifyLoadFailed();
                    }
                });
                break;
        }
    }

    private void notifyLoadStarted() {
        if (mOnGlideProgressListener != null) {
            mOnGlideProgressListener.onProgress(0);
        }
        if (mProgressListener != null) {
            ProgressInterceptor.addProgressListener(mImageUrl, mProgressListener);
        }
    }

    private void notifyResourceReady() {
        if (mOnGlideProgressListener != null) {
            mOnGlideProgressListener.onProgress(1);
        }
        if (mProgressListener != null) {
            ProgressInterceptor.removeProgressListener(mProgressListener);
        }
    }

    private void notifyLoadFailed() {
        if (mOnGlideProgressListener != null) {
            mOnGlideProgressListener.onProgress(-1);
        }
        if (mProgressListener != null) {
            ProgressInterceptor.removeProgressListener(mProgressListener);
        }
    }

    public void preload() {
        switch (as) {
            case DRAWABLE:
                getBuilder().apply(getOptions()).preload();
                break;
            case GIF:
                getGifBuilder().apply(getOptions()).preload();
                break;
            case BITMAP:
                getBmpBuilder().apply(getOptions()).preload();
                break;
        }
    }

    public void getGif(final SimpleCallback<GifDrawable> callback) {
        getGif(callback, null);
    }

    public void getGif(final SimpleCallback<GifDrawable> callback, final SimpleListener onFail) {
        getGifBuilder().apply(getOptions()).into(new SimpleTarget<GifDrawable>() {
            @Override
            public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                if (callback != null) {
                    callback.onResult(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (onFail != null) {
                    onFail.onResult();
                }
            }
        });
    }

    public void getDrawable(final SimpleCallback<Drawable> callback) {
        getDrawable(callback, null);
    }

    public void getDrawable(final SimpleCallback<Drawable> callback, final SimpleListener onFail) {
        getBuilder().apply(getOptions()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onResult(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (onFail != null) {
                    onFail.onResult();
                }
            }
        });
    }

    public void getBitmap(final SimpleCallback<Bitmap> callback) {
        getBitmap(callback, null);
    }

    public void getBitmap(final SimpleCallback<Bitmap> onSuccess, final SimpleListener onFail) {
        getBmpBuilder().apply(getOptions()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (onSuccess != null) {
                    onSuccess.onResult(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (onFail != null) {
                    onFail.onResult();
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private RequestOptions getOptions() {
        RequestOptions options = new RequestOptions();
        if (mPlaceHolder > 0) {
            options.placeholder(mPlaceHolder);
        }
        if (mErrorHolder > 0) {
            options.error(mErrorHolder);
        }
        if (mCache) {
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
        } else {
            options.skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        if (mBitmapTransformation != null) {
            options.transform(mBitmapTransformation);
        }
        return options;
    }

    private Context checkContext(Context context) {
        if (context != null) {
            return context;
        }
        return Utils.getAppContext();
    }

    private RequestBuilder<Bitmap> getBmpBuilder() {
        if (mBuilderBmp == null) {
            mBuilderBmp = mManager.asBitmap();
        }
        return mBuilderBmp;
    }

    private RequestBuilder<GifDrawable> getGifBuilder() {
        if (mBuilderGif == null) {
            mBuilderGif = mManager.asGif();
        }
        return mBuilderGif;
    }

    private RequestBuilder<Drawable> getBuilder() {
        if (mBuilder == null) {
            mBuilder = mManager.asDrawable();
        }
        return mBuilder;
    }

    public interface OnGlideProgressListener {
        void onProgress(float progress);
    }
}
