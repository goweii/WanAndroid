package per.goweii.wanandroid.utils;

import per.goweii.basic.utils.SPUtils;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class UserInfoUtils {

    private static final String SP_NAME = "user_info";
    private static final String KEY_ICON = "KEY_ICON";
    private static final String KEY_BG = "KEY_BG";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private static class Holder {
        private static final UserInfoUtils INSTANCE = new UserInfoUtils();
    }

    public static UserInfoUtils getInstance() {
        return Holder.INSTANCE;
    }

    private UserInfoUtils() {
    }

    public void setIcon(String icon) {
        mSPUtils.save(KEY_ICON, icon);
    }

    public String getIcon() {
        return mSPUtils.get(KEY_ICON, "");
    }

    public void setBg(String icon) {
        mSPUtils.save(KEY_BG, icon);
    }

    public String getBg() {
        return mSPUtils.get(KEY_BG, "");
    }
}
