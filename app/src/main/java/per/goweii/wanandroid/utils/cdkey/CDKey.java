package per.goweii.wanandroid.utils.cdkey;

import android.support.annotation.NonNull;

/**
 * @author CuiZhen
 * @date 2020/1/1
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface CDKey {
    @NonNull
    String createCDKey(@NonNull String userId);

    boolean active(@NonNull String userId, @NonNull String cdkey);
}
