package per.goweii.wanandroid.http;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface CacheListener<E> {
    void onSuccess(int code, E data);
    void onFailed();
}
