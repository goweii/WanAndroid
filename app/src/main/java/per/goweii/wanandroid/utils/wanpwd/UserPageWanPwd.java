package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.router.Param;
import per.goweii.wanandroid.utils.router.RouterMap;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class UserPageWanPwd implements IWanPwd {

    private final String content;
    private String mShowText;
    private String mBtnText;
    private Runnable mRunnable;

    public UserPageWanPwd(String content) {
        this.content = content;
        parse();
    }

    @Nullable
    @Override
    public Runnable getRunnable() {
        return mRunnable;
    }

    @Override
    public String getShowText() {
        return mShowText;
    }

    @Override
    public String getBtnText() {
        return mBtnText;
    }

    private void parse() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                stringBuilder.append(c);
            }
        }
        int userId = -1;
        try {
            userId = Integer.parseInt(stringBuilder.toString());
        } catch (Exception ignore) {
        }
        mShowText = ResUtils.getString(R.string.you_ve_discovered_a_mystery_user);
        mBtnText = ResUtils.getString(R.string.go_to_the_homepage);
        int finalUserId = userId;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                RouterMap.USER_PAGE.navigation(new Param("id", "" + finalUserId));
            }
        };
    }
}
