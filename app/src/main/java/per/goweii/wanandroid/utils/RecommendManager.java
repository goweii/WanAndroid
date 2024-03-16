package per.goweii.wanandroid.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.model.RecommendBean;

public class RecommendManager {
    private static volatile RecommendManager sInstance;
    private final RxLife mRxLife;

    private volatile boolean mLoading = false;
    private volatile RecommendBean mRecommendBean;

    private final List<Callback> mCallbacks = new ArrayList<>();

    private RecommendManager() {
        mRxLife = RxLife.create();
    }

    @NonNull
    public static RecommendManager getInstance() {
        if (sInstance == null) {
            synchronized (RecommendManager.class) {
                if (sInstance == null) {
                    sInstance = new RecommendManager();
                }
            }
        }
        return sInstance;
    }

    public synchronized void getBean(@NonNull Callback callback) {
        if (mLoading) {
            mCallbacks.add(callback);
        } else {
            if (mRecommendBean != null) {
                callback.onResult(mRecommendBean);
            } else {
                mCallbacks.add(callback);
                load();
            }
        }
    }

    public synchronized void load() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        MainRequest.getRecommend(mRxLife, new RequestListener<RecommendBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, RecommendBean data) {
                mRecommendBean = data;
            }

            @Override
            public void onFailed(int code, String msg) {
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                mLoading = false;
                if (!mCallbacks.isEmpty()) {
                    for (Callback callback : mCallbacks) {
                        callback.onResult(mRecommendBean);
                    }
                }
            }
        });
    }

    public interface Callback {
        void onResult(@Nullable RecommendBean bean);
    }
}
