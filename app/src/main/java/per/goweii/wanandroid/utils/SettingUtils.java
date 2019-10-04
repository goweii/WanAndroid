package per.goweii.wanandroid.utils;

import per.goweii.basic.utils.SPUtils;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class SettingUtils {

    private static final String SP_NAME = "setting";
    private static final String KEY_DARK_THEME = "KEY_DARK_THEME";
    private static final String KEY_SHOW_READ_LATER = "KEY_SHOW_READ_LATER";
    private static final String KEY_SHOW_TOP = "KEY_SHOW_TOP";
    private static final String KEY_SHOW_BANNER = "KEY_SHOW_BANNER";
    private static final String KEY_HIDE_ABOUT_ME = "KEY_HIDE_ABOUT_ME";
    private static final String KEY_HIDE_OPEN = "KEY_HIDE_OPEN";
    private static final String KEY_WEB_SWIPE_BACK_EDGE = "KEY_WEB_SWIPE_BACK_EDGE";
    private static final String KEY_RV_ANIM = "KEY_RV_ANIM";
    private static final String KEY_URL_INTERCEPT_TYPE = "KEY_URL_INTERCEPT_TYPE";
    private static final String KEY_SEARCH_HISTORY_MAX_COUNT = "KEY_SEARCH_HISTORY_MAX_COUNT";
    private static final String KEY_UPDATE_IGNORE_DURATION = "KEY_UPDATE_IGNORE_DURATION";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private boolean mDarkTheme = false;
    private boolean mShowReadLater = true;
    private boolean mShowTop = true;
    private boolean mShowBanner = true;
    private boolean mHideAboutMe = false;
    private boolean mHideOpen = false;
    private boolean mWebSwipeBackEdge = true;
    private int mRvAnim = RvAnimUtils.RvAnim.NONE;
    private int mUrlInterceptType = WebUrlInterceptUtils.TYPE_ONLY_WHITE;
    private int mSearchHistoryMaxCount = 20;
    private long mUpdateIgnoreDuration = 7 * 24 * 60 * 60 * 1000L;

    private static class Holder {
        private static final SettingUtils INSTANCE = new SettingUtils();
    }

    public static SettingUtils getInstance() {
        return Holder.INSTANCE;
    }

    private SettingUtils() {
        mDarkTheme = mSPUtils.get(KEY_DARK_THEME, mDarkTheme);
        mShowReadLater = mSPUtils.get(KEY_SHOW_READ_LATER, mShowReadLater);
        mShowTop = mSPUtils.get(KEY_SHOW_TOP, mShowTop);
        mShowBanner = mSPUtils.get(KEY_SHOW_BANNER, mShowBanner);
        mHideAboutMe = mSPUtils.get(KEY_HIDE_ABOUT_ME, mHideAboutMe);
        mHideOpen = mSPUtils.get(KEY_HIDE_OPEN, mHideOpen);
        mWebSwipeBackEdge = mSPUtils.get(KEY_WEB_SWIPE_BACK_EDGE, mWebSwipeBackEdge);
        mRvAnim = mSPUtils.get(KEY_RV_ANIM, mRvAnim);
        mUrlInterceptType = mSPUtils.get(KEY_URL_INTERCEPT_TYPE, mUrlInterceptType);
        mSearchHistoryMaxCount = mSPUtils.get(KEY_SEARCH_HISTORY_MAX_COUNT, mSearchHistoryMaxCount);
        mUpdateIgnoreDuration = mSPUtils.get(KEY_UPDATE_IGNORE_DURATION, mUpdateIgnoreDuration);
    }

    public void setDarkTheme(boolean darkTheme) {
        mDarkTheme = darkTheme;
        mSPUtils.save(KEY_DARK_THEME, darkTheme);
    }

    public boolean isDarkTheme() {
        return mDarkTheme;
    }

    public void setShowReadLater(boolean showReadLater) {
        mShowReadLater = showReadLater;
        mSPUtils.save(KEY_SHOW_READ_LATER, showReadLater);
    }

    public boolean isShowReadLater() {
        return mShowReadLater;
    }

    public void setShowTop(boolean showTop) {
        mShowTop = showTop;
        mSPUtils.save(KEY_SHOW_TOP, showTop);
    }

    public boolean isShowTop() {
        return mShowTop;
    }

    public void setShowBanner(boolean showBanner) {
        mShowBanner = showBanner;
        mSPUtils.save(KEY_SHOW_BANNER, showBanner);
    }

    public boolean isShowBanner() {
        return mShowBanner;
    }

    public void setHideAboutMe(boolean hideAboutMe) {
        mHideAboutMe = hideAboutMe;
        mSPUtils.save(KEY_HIDE_ABOUT_ME, hideAboutMe);
    }

    public boolean isHideAboutMe() {
        return mHideAboutMe;
    }

    public void setHideOpen(boolean hideOpen) {
        mHideOpen = hideOpen;
        mSPUtils.save(KEY_HIDE_OPEN, hideOpen);
    }

    public boolean isHideOpen() {
        return mHideOpen;
    }

    public void setWebSwipeBackEdge(boolean webSwipeBackEdge) {
        mWebSwipeBackEdge = webSwipeBackEdge;
        mSPUtils.save(KEY_WEB_SWIPE_BACK_EDGE, webSwipeBackEdge);
    }

    public boolean isWebSwipeBackEdge() {
        return mWebSwipeBackEdge;
    }

    public void setUrlInterceptType(int type) {
        mUrlInterceptType = type;
        mSPUtils.save(KEY_URL_INTERCEPT_TYPE, type);
    }

    public int getUrlInterceptType() {
        return mUrlInterceptType;
    }

    public void setRvAnim(int anim) {
        mRvAnim = anim;
        mSPUtils.save(KEY_RV_ANIM, anim);
    }

    public int getRvAnim() {
        return mRvAnim;
    }

    public void setSearchHistoryMaxCount(int count) {
        mSearchHistoryMaxCount = count;
        mSPUtils.save(KEY_SEARCH_HISTORY_MAX_COUNT, count);
    }

    public int getSearchHistoryMaxCount() {
        return mSearchHistoryMaxCount;
    }

    public void setUpdateIgnoreDuration(long dur) {
        mUpdateIgnoreDuration = dur;
        mSPUtils.save(KEY_UPDATE_IGNORE_DURATION, dur);
    }

    public long getUpdateIgnoreDuration() {
        return mUpdateIgnoreDuration;
    }
}
