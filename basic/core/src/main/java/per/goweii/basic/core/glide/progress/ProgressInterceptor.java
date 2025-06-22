package per.goweii.basic.core.glide.progress;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.basic.utils.LogUtils;

/**
 * 描述：加载进度拦截器替换ProgressResponseBody
 *
 * @author Cuizhen
 * @date 2018/9/17
 */
public class ProgressInterceptor implements Interceptor {
    private static final String TAG = ProgressInterceptor.class.getSimpleName();

    private static final Map<String, WeakReference<OnProgressListener>> LISTENER_MAP = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Response intercept(Chain chain) throws IOException {
        LogUtils.d(TAG, "intercept chain");
        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        OnProgressListener progressListener = getProgressListener(url);
        OnProgressResponseListener onProgressResponseListener = null;
        if (progressListener != null) {
            onProgressResponseListener = new OnProgressResponseListener(progressListener);
        }
        return response.newBuilder()
                .body(new ProgressResponseBody(url, response.body(), onProgressResponseListener))
                .build();
    }

    private static final class OnProgressResponseListener implements ProgressResponseBody.OnResponseListener {

        private final OnProgressListener mListener;

        private OnProgressResponseListener(@NonNull OnProgressListener listener) {
            mListener = listener;
        }

        @Override
        public void onStart(String url, long totalBytes) {
            mListener.onProgress(0);
            LogUtils.i(TAG, "onStart totalBytes=" + totalBytes);
        }

        @Override
        public void onRead(String url, long readBytes, long totalBytes) {
            float progress = (float) readBytes / (float) totalBytes;
            mListener.onProgress(progress);
            LogUtils.i(TAG, "onRead readBytes=" + readBytes + ",totalBytes=" + totalBytes);
        }

        @Override
        public void onDone(String url, long totalBytes) {
            mListener.onProgress(1);
            removeProgressListener(url);
            LogUtils.i(TAG, "onDone totalBytes=" + totalBytes);
        }

        @Override
        public void onClose(String url, long readBytes, long totalBytes) {
            if (readBytes == totalBytes) {
                mListener.onProgress(1);
            } else {
                mListener.onProgress(0);
            }
            removeProgressListener(url);
        }

        @Override
        public void onTimeout(String url, long readBytes, long totalBytes) {
            mListener.onProgress(-1);
            removeProgressListener(url);
            LogUtils.i(TAG, "onFail");
        }
    }

    public static OnProgressListener getProgressListener(String url) {
        if (LISTENER_MAP != null && LISTENER_MAP.size() > 0) {
            Iterator<Map.Entry<String, WeakReference<OnProgressListener>>> iterator = LISTENER_MAP.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, WeakReference<OnProgressListener>> entry = iterator.next();
                String key = entry.getKey();
                OnProgressListener progressListener = entry.getValue().get();
                if (progressListener == null) {
                    iterator.remove();
                    continue;
                }
                if (TextUtils.equals(key, url)) {
                    return progressListener;
                }
            }
        }
        return null;
    }

    public static void addProgressListener(String url, OnProgressListener progressListener) {
        if (progressListener == null) {
            return;
        }
        LISTENER_MAP.put(url, new WeakReference<>(progressListener));
    }

    public static void removeProgressListener(OnProgressListener listener) {
        Iterator<Map.Entry<String, WeakReference<OnProgressListener>>> iterator = LISTENER_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WeakReference<OnProgressListener>> entry = iterator.next();
            OnProgressListener progressListener = entry.getValue().get();
            if (progressListener == listener) {
                iterator.remove();
            }
        }
    }

    public static void removeProgressListener(String url) {
        Iterator<String> iterator = LISTENER_MAP.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.equals(url, key)) {
                iterator.remove();
            }
        }
    }
}
