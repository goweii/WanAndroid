package per.goweii.wanandroid.http;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import per.goweii.rxhttp.request.RxRequest;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.basic.core.receiver.LoginReceiver;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class BaseRequest {

    protected static <T> Disposable request(@NonNull Observable<WanResponse<T>> observable, @NonNull RequestListener<T> listener) {
        return RxRequest.create(observable)
                .listener(new RxRequest.RequestListener() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle handle) {
                        listener.onError(handle);
                        listener.onFailed(WanApi.ApiCode.ERROR, handle.getMsg());
                    }

                    @Override
                    public void onFinish() {
                        listener.onFinish();
                    }
                })
                .request(new RxRequest.ResultCallback<T>() {
                    @Override
                    public void onSuccess(int code, T data) {
                        listener.onSuccess(code, data);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        if (code == WanApi.ApiCode.FAILED_NOT_LOGIN) {
                            UserUtils.getInstance().logout();
                            LoginReceiver.sendNotLogin();
                        }
                        listener.onFailed(code, msg);
                    }
                });
    }

}
