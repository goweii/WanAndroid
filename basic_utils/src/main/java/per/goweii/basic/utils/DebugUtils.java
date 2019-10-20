package per.goweii.basic.utils;

import android.text.TextUtils;

/**
 * @author CuiZhen
 * @date 2019/10/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class DebugUtils {

    public static boolean isDebug() {
        return BuildConfig.DEBUG && TextUtils.equals(BuildConfig.BUILD_TYPE, "debug");
    }

}
