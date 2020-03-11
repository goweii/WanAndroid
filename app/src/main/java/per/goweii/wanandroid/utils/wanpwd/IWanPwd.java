package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public interface IWanPwd {
    @Nullable
    Runnable getRunnable();

    String getShowText();

    String getBtnText();
}
