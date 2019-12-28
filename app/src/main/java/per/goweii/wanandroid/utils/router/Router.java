package per.goweii.wanandroid.utils.router;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import per.goweii.basic.utils.LogUtils;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class Router {
    static final String SCHEME = "wana";
    static final String HOST = "www.wanandroid.com";

    static final String PARAM_URL = "ROUTER_URL";

    public static void router(String url) {
        LogUtils.d("Router", "url=" + url);
        if (url == null) return;
        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        if (scheme == null) return;
        if (TextUtils.equals("http", scheme) || TextUtils.equals("https", scheme)) {
            RouterMap.WEB.navigation(url);
            return;
        }
        if (!TextUtils.equals(SCHEME, scheme)) return;
        final String host = uri.getHost();
        if (host == null) return;
        if (!TextUtils.equals(HOST, host)) return;
        final String path = uri.getPath();
        if (path == null) return;
        final RouterMap routerMap = RouterMap.from(path);
        if (routerMap == RouterMap.NULL) return;
        routerMap.navigation(url);
    }

    @Nullable
    public static Uri uri(Intent intent) {
        String url = intent.getStringExtra(PARAM_URL);
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return Uri.parse(url);
    }
}
