package per.goweii.wanandroid.utils;

import android.text.TextUtils;

/**
 * @author CuiZhen
 * @date 2019/9/4
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebUrlInterceptUtils {

    public static final int TYPE_NOTHING = 0;
    public static final int TYPE_ONLY_WHITE = 1;
    public static final int TYPE_INTERCEPT_BLACK = 2;

    public static String getName(int type) {
        String name = "";
        switch (type) {
            default:
                break;
            case TYPE_NOTHING:
                name = "不拦截";
                break;
            case TYPE_ONLY_WHITE:
                name = "仅放行白名单";
                break;
            case TYPE_INTERCEPT_BLACK:
                name = "仅拦截黑名单";
                break;
        }
        return name;
    }

    private static final String[] WHITE_HOST = new String[]{
            "www.wanandroid.com",
            "study.163.com",
            "juejin.im",
            "www.jianshu.com",
            "mp.weixin.qq.com",
            "blog.csdn.net",
            "github.com",
            "gitee.com",
            "www.oschina.net"
    };

    private static final String[] BLACK_HOST = new String[]{
            "www.taobao.com",
            "www.jd.com"
    };

    public static boolean isWhiteHost(String host) {
        for (String s : WHITE_HOST) {
            if (TextUtils.equals(s, host)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlackHost(String host) {
        for (String s : BLACK_HOST) {
            if (TextUtils.equals(s, host)) {
                return true;
            }
        }
        return false;
    }
}
