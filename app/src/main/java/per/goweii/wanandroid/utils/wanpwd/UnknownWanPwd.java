package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.router.RouterMap;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class UnknownWanPwd implements IWanPwd {
    @Nullable
    @Override
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                RouterMap.SETTING.navigation();
            }
        };
    }

    @Override
    public String getShowText() {
        return ResUtils.getString(R.string.you_ve_discovered_a_mysterious_password);
    }

    @Override
    public String getBtnText() {
        return ResUtils.getString(R.string.go_to_update);
    }
}
