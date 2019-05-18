package per.goweii.wanandroid.http;

import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public abstract class RequestCallback<E> implements RequestListener<E> {
    @Override
    public void onStart() {
    }

    @Override
    public void onError(ExceptionHandle handle) {
    }

    @Override
    public void onFinish() {
    }
}