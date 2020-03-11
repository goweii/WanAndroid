package per.goweii.wanandroid.http;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface CacheListener<E> {
    void onSuccess(int code, E data);
    void onFailed();
}
