package per.goweii.wanandroid.utils.router;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import per.goweii.basic.utils.Utils;
import per.goweii.wanandroid.module.home.activity.UserPageActivity;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.activity.AboutMeActivity;
import per.goweii.wanandroid.module.mine.activity.SettingActivity;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public enum RouterMap {

    NULL(null, null),
    WEB("/main/web", WebActivity.class),
    USER_PAGE("/main/user_page", UserPageActivity.class),
    SETTING("/mine/setting", SettingActivity.class),
    ABOUT_ME("/mine/about_me", AboutMeActivity.class);

    private final String path;
    private final Class<? extends Activity> clazz;

    RouterMap(String path, Class<? extends Activity> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    @NonNull
    public static RouterMap from(String path) {
        for (RouterMap routerMap : RouterMap.values()) {
            if (TextUtils.equals(routerMap.path, path)) {
                return routerMap;
            }
        }
        return NULL;
    }

    public String url() {
        // wana://www.wanandroid.com/user_page?id=1
        return Router.SCHEME + "://" + Router.HOST + path;
    }

    public String url(Param... param) {
        StringBuilder s = new StringBuilder(url());
        for (int i = 0; i < param.length; i++) {
            Param p = param[i];
            if (i == 0) s.append("?");
            else s.append("&");
            s.append(p.key).append("=").append(p.value);
        }
        return s.toString();
    }

    public void navigation(String url) {
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
