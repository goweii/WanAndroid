package per.goweii.wanandroid.utils.wanpwd;

import android.support.annotation.Nullable;

import per.goweii.wanandroid.utils.router.Router;
import per.goweii.wanandroid.utils.router.RouterMap;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AboutMeWanPwd implements IWanPwd {
    @Nullable
    @Override
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                Router.router(RouterMap.ABOUT_ME.url());
            }
        };
    }

    @Override
    public String getShowText() {
        return "请开发小哥哥喝杯咖啡吧！\n一个完全免费的APP！\n一个这么好用还完全免费的APP！\n一个这么好看又好用还完全免费的APP！\n不请我喝杯咖啡提提神？\n我都快没精力继续维护了！";
    }

    @Override
    public String getBtnText() {
        return "去请客";
    }
}
