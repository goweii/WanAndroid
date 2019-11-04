package per.goweii.basic.core.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
    private RequestBuilder<Bitmap> mBuilder;
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

    public GlideHelper load(String url) {
        this.mImageUrl = url;
        mBuilder = getBuilder().load(url);
        return this;
    }

    public GlideHelper load(Uri uri) {
        mBuilder = getBuilder().load(uri);
        return this;
    }

    public GlideHelper load(int resId) {
        mBuilder = getBuilder().load(resId);
        return this;
    }

    public GlideHelper load(Bitmap bitmap) {
        mBuilder = getBuilder().load(bitmap);
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
        getBuilder().apply(getOptions()).into(new BitmapImageViewTarget(imageView) {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                if (mOnGlideProgressListener != null) {
                    mOnGlideProgressListener.onProgress(0);
                }
                if (mProgressListener != null) {
                    ProgressInterceptor.addProgressListener(mImageUrl, mProgressListener);
                }
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
                if (mOnGlideProgressListener != null) {
                    mOnGlideProgressListener.onProgress(1);
                }
                if (mProgressListener != null) {
                    ProgressInterceptor.removeProgressListener(mProgressListener);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (mOnGlideProgressListener != null) {
                    mOnGlideProgressListener.onProgress(-1);
                }
                if (mProgressListener != null) {
                    ProgressInterceptor.removeProgressListener(mProgressListener);
                }
            }
        });
    }

    public void preload() {
        getBuilder().apply(getOptions()).preload();
    }

    public void get(final SimpleCallback<Bitmap> callback) {
        getBuilder().apply(getOptions()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (callback != null) {
                    callback.onResult(resource);
                }
            }
        });
    }

    public void get(final SimpleCallback<Bitmap> onSuccess, final SimpleListener onFail) {
        getBuilder().apply(getOptions()).into(new SimpleTarget<Bitmap>() {
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

    private RequestBuilder<Bitmap> getBuilder() {
        if (mBuilder == null) {
            mBuilder = mManager.asBitmap();
        }
        return mBuilder;
    }

    public interface OnGlideProgressListener {
        void onProgress(float progress);
    }
}
