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
public class AboutMeWanPwd implements IWanPwd {
    @Nullable
    @Override
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                RouterMap.ABOUT_ME.navigation();
            }
        };
    }

    @Override
    public String getShowText() {
        return ResUtils.getString(R.string.let_s_have_a_cup_of_coffee);
    }

    @Override
    public String getBtnText() {
        return ResUtils.getString(R.string.go_for_a_treat);
    }
}
