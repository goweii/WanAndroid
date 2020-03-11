package per.goweii.basic.utils;

import android.text.TextUtils;

/**
 * @author CuiZhen
 * @date 2019/10/20
 * GitHub: https://github.com/goweii
 */
public class DebugUtils {

    private static final boolean DEBUG;

    static {
        DEBUG = BuildConfig.DEBUG && TextUtils.equals(BuildConfig.BUILD_TYPE, "debug");
    }

    public static boolean isDebug() {
        return DEBUG;
    }

}
