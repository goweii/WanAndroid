package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.basic.utils.AppOpenUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.Utils;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class QQWanPwd implements IWanPwd {

    private final String content;

    public QQWanPwd(String content) {
        this.content = content;
    }

    @Nullable
    @Override
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (AppInfoUtils.isAppInstalled(Utils.getAppContext(), AppInfoUtils.PackageName.QQ)) {
                    AppOpenUtils.openQQChat(Utils.getAppContext(), getQQ());
                }
            }
        };
    }

    @Override
    public String getShowText() {
        return String.format(ResUtils.getString(R.string.you_have_found_a_qq_number), getQQ());
    }

    @Override
    public String getBtnText() {
        return ResUtils.getString(R.string.add_friends);
    }

    private String getQQ() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                s.append(c);
            }
        }
        return s.toString();
    }
}
