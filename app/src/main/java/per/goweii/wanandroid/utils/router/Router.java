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
        RouterMap.from(path).navigation(url);
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
