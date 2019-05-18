package per.goweii.wanandroid.http;

import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface RequestListener<E> {
    void onStart();
    void onSuccess(int code, E data);
    void onFailed(int code, String msg);
    void onError(ExceptionHandle handle);
    void onFinish();
}
