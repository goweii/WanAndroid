package per.goweii.wanandroid.utils;

import per.goweii.basic.utils.SPUtils;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class GuideSPUtils {

    private static final String SP_NAME = "guide";
    private static final String KEY_WEB_GUIDE = "KEY_WEB_GUIDE";
    private static final String KEY_PRIVACY_POLICY = "KEY_PRIVACY_POLICY";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private static class Holder {
        private static final GuideSPUtils INSTANCE = new GuideSPUtils();
    }

    public static GuideSPUtils getInstance() {
        return Holder.INSTANCE;
    }

    private GuideSPUtils() {
    }

    public boolean isWebGuideShown() {
        return mSPUtils.get(KEY_WEB_GUIDE, false);
    }

    public void setWebGuideShown() {
        mSPUtils.save(KEY_WEB_GUIDE, true);
    }

    public boolean isPrivacyPolicyShown() {
        return mSPUtils.get(KEY_PRIVACY_POLICY, false);
    }

    public void setPrivacyPolicyShown() {
        mSPUtils.save(KEY_PRIVACY_POLICY, true);
    }

}
