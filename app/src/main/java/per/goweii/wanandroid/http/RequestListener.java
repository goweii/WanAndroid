package per.goweii.wanandroid.http;

import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface RequestListener<E> {
    void onStart();
    void onSuccess(int code, E data);
    void onFailed(int code, String msg);
    void onError(ExceptionHandle handle);
    void onFinish();
}
