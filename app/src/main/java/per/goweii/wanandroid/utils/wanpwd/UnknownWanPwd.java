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
public class UnknownWanPwd implements IWanPwd {
    @Nullable
    @Override
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                Router.router(RouterMap.SETTING.url());
            }
        };
    }

    @Override
    public String getShowText() {
        return "你发现了一个神秘口令！\n可惜当前版本不支持，快去设置中更新版本再试试吧！";
    }

    @Override
    public String getBtnText() {
        return "去更新";
    }
}
