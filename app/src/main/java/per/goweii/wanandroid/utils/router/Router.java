package per.goweii.wanandroid.utils.router;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import per.goweii.basic.utils.LogUtils;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class Router {
    public static final String SCHEME = "wana";
    public static final String HOST = "www.wanandroid.com";

    static final String PARAM_URL = "ROUTER_URL";

    public static String createUrlByPath(String path) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Router.SCHEME);
        urlBuilder.append("://");
        urlBuilder.append(Router.HOST);
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append(path);
        }
        return urlBuilder.toString();
    }

    public static void routeTo(String url) {
        LogUtils.d("Router", "url=" + url);
        if (url == null) return;
        final Uri uri = Uri.parse(url);
        if (checkHost(uri)) {
            RouterMap routerMap = RouterMap.from(uri);
            if (routerMap.isExist()) {
                routerMap.navigation(url);
            } else {
                if (isHttpOrHttps(uri)) {
                    RouterMap.WEB.navigation(url);
                }
            }
        } else {
            if (isHttpOrHttps(uri)) {
                RouterMap.WEB.navigation(url);
            }
        }
    }

    @Nullable
    public static Uri getUriFrom(Intent intent) {
        String url = intent.getStringExtra(PARAM_URL);
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return Uri.parse(url);
    }

    private static boolean checkHost(Uri uri) {
        final String host = uri.getHost();
        return TextUtils.equals(HOST, host);
    }

    private static boolean checkScheme(Uri uri) {
        final String scheme = uri.getScheme();
        return TextUtils.equals(SCHEME, scheme);
    }

    private static boolean isHttpOrHttps(Uri uri) {
        final String scheme = uri.getScheme();
        return TextUtils.equals("http", scheme) || TextUtils.equals("https", scheme);
    }
}
