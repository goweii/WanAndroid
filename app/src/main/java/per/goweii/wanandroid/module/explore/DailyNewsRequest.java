package per.goweii.wanandroid.module.explore;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.basic.core.utils.JsonFormatUtils;
import per.goweii.basic.utils.LogUtils;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.rxhttp.request.RxRequest;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.CacheListener;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;

@SuppressLint("SimpleDateFormat")
public class DailyNewsRequest extends BaseRequest {
    public static void getDailyNews(@NonNull RxLife rxLife,
                                    @NonNull RequestListener<List<DailyNewsBean>> listener) {
        WanCache.getInstance().getBean(
                WanCache.CacheKey.DAILY_NEWS,
                DailyNewsResponse.class,
                new CacheListener<DailyNewsResponse>() {
                    @Override
                    public void onSuccess(int code, final DailyNewsResponse data) {
                        final String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        if (TextUtils.equals(data.getDate(), date)) {
                            listener.onSuccess(code, data.getData());
                        } else {
                            getDailyNewsFromNet(rxLife, listener);
                        }
                    }

                    @Override
                    public void onFailed() {
                        getDailyNewsFromNet(rxLife, listener);
                    }
                });
    }

    public static void getDailyNewsFromCache(@NonNull RequestListener<List<DailyNewsBean>> listener) {
        WanCache.getInstance().getBean(
                WanCache.CacheKey.DAILY_NEWS,
                DailyNewsResponse.class,
                new CacheListener<DailyNewsResponse>() {
                    @Override
                    public void onSuccess(int code, final DailyNewsResponse data) {
                        listener.onSuccess(code, data.getData());
                    }

                    @Override
                    public void onFailed() {
                    }
                });
    }

    public static void getDailyNewsFromNet(@NonNull RxLife rxLife,
                                            @NonNull RequestListener<List<DailyNewsBean>> listener) {
        final Disposable disposable = RxRequest.create(WanApi.api().getDailyNews(DailyNewsPlatform.JUEJIN.getPlatformCode()))
                .listener(new RxRequest.RequestListener() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle handle) {
                        handle.getException().printStackTrace();
                        LogUtils.httpe(handle.getMsg());
                        listener.onError(handle);
                        listener.onFailed(WanApi.ApiCode.ERROR, handle.getMsg());
                    }

                    @Override
                    public void onFinish() {
                        listener.onFinish();
                    }
                })
                .request(new RxRequest.ResultCallback<List<DailyNewsBean>>() {
                    @Override
                    public void onSuccess(int code, List<DailyNewsBean> data) {
                        LogUtils.httpi(JsonFormatUtils.INSTANCE.toJson(data));
                        final String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        final DailyNewsResponse response = new DailyNewsResponse();
                        response.setData(data);
                        response.setDate(date);
                        WanCache.getInstance().save(WanCache.CacheKey.DAILY_NEWS, response);
                        listener.onSuccess(code, data);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        listener.onFailed(code, msg);
                    }
                });
        rxLife.add(disposable);
    }
}
