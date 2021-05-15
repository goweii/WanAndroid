package per.goweii.wanandroid.utils.router;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import per.goweii.basic.utils.Utils;
import per.goweii.wanandroid.module.home.activity.UserPageActivity;
import per.goweii.wanandroid.module.main.activity.ArticleListActivity;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.activity.AboutMeActivity;
import per.goweii.wanandroid.module.mine.activity.SettingActivity;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public enum RouterMap {
    NULL(null, null),
    WEB("/main/web", WebActivity.class),
    USER_PAGE("/main/user_page", UserPageActivity.class),
    SETTING("/mine/setting", SettingActivity.class),
    ABOUT_ME("/mine/about_me", AboutMeActivity.class),
    ARTICLE_LIST("/main/article_list", ArticleListActivity.class);

    private final String path;
    private final Class<? extends Activity> clazz;

    RouterMap(String path, Class<? extends Activity> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    @NonNull
    public static RouterMap from(String path) {
        if (!TextUtils.isEmpty(path)) {
            for (RouterMap routerMap : RouterMap.values()) {
                if (TextUtils.equals(routerMap.path, path)) {
                    return routerMap;
                }
            }
        }
        return NULL;
    }

    @NonNull
    public static RouterMap from(Uri uri) {
        return from(uri.getPath());
    }

    public boolean isExist() {
        return this != NULL;
    }

    public boolean isNull() {
        return this == NULL;
    }

    public boolean isWeb() {
        return this == WEB;
    }

    public String url() {
        return Router.createUrlByPath(path);
    }

    public String url(Param... param) {
        StringBuilder s = new StringBuilder(url());
        for (Param p : param) {
            if (s.indexOf("?") != -1) s.append("?");
            else s.append("&");
            s.append(p.key).append("=").append(p.value);
        }
        return s.toString();
    }

    public void navigation(Param... param) {
        navigation(url(param));
    }

    void navigation(String url) {
        if (clazz == null) return;
        try {
            Intent intent = new Intent(Utils.getAppContext(), clazz);
            intent.putExtra(Router.PARAM_URL, url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Utils.getAppContext().startActivity(intent);
        } catch (Exception ignore) {
        }
    }
}
