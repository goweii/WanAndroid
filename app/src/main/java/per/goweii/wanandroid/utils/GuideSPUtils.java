package per.goweii.wanandroid.utils;

import per.goweii.basic.utils.SPUtils;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class GuideSPUtils {

    private static final String SP_NAME = "guide";
    private static final String KEY_WEB_GUIDE = "KEY_WEB_GUIDE";
    private static final String KEY_ARTICLE_GUIDE = "KEY_ARTICLE_GUIDE";
    private static final String KEY_PRIVACY_POLICY = "KEY_PRIVACY_POLICY";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private boolean webGuideShown = false;
    private boolean articleGuideShown = false;
    private boolean privacyPolicyShown = false;

    private static class Holder {
        private static final GuideSPUtils INSTANCE = new GuideSPUtils();
    }

    public static GuideSPUtils getInstance() {
        return Holder.INSTANCE;
    }

    private GuideSPUtils() {
    }

    public boolean isWebGuideShown() {
        if (webGuideShown) return true;
        webGuideShown = mSPUtils.get(KEY_WEB_GUIDE, false);
        return webGuideShown;
    }

    public void setWebGuideShown() {
        webGuideShown = true;
        mSPUtils.save(KEY_WEB_GUIDE, true);
    }

    public boolean isArticleGuideShown() {
        if (articleGuideShown) return true;
        articleGuideShown = mSPUtils.get(KEY_ARTICLE_GUIDE, false);
        return articleGuideShown;
    }

    public void setArticleGuideShown() {
        articleGuideShown = true;
        mSPUtils.save(KEY_ARTICLE_GUIDE, true);
    }

    public boolean isPrivacyPolicyShown() {
        if (privacyPolicyShown) return true;
        privacyPolicyShown = mSPUtils.get(KEY_PRIVACY_POLICY, false);
        return privacyPolicyShown;
    }

    public void setPrivacyPolicyShown() {
        privacyPolicyShown = true;
        mSPUtils.save(KEY_PRIVACY_POLICY, true);
    }

}
