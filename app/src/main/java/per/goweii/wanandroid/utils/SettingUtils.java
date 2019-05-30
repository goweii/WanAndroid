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
    private static final String KEY_SHOW_READ_LATER = "KEY_SHOW_READ_LATER";
    private static final String KEY_SHOW_TOP = "KEY_SHOW_TOP";
    private static final String KEY_HIDE_ABOUT_ME = "KEY_HIDE_ABOUT_ME";
    private static final String KEY_HIDE_OPEN = "KEY_HIDE_OPEN";
    private static final String KEY_WEB_SWIPE_BACK_EDGE = "KEY_WEB_SWIPE_BACK_EDGE";
    private static final String KEY_RV_ANIM = "KEY_RV_ANIM";
    private static final String KEY_SEARCH_HISTORY_MAX_COUNT = "KEY_SEARCH_HISTORY_MAX_COUNT";
    private static final String KEY_UPDATE_IGNORE_DURATION = "KEY_UPDATE_IGNORE_DURATION";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private static class Holder {
        private static final SettingUtils INSTANCE = new SettingUtils();
    }

    public static SettingUtils getInstance() {
        return Holder.INSTANCE;
    }

    private SettingUtils() {
    }

    public void setShowReadLater(boolean showReadLater) {
        mSPUtils.save(KEY_SHOW_READ_LATER, showReadLater);
    }

    public boolean isShowReadLater() {
        return mSPUtils.get(KEY_SHOW_READ_LATER, true);
    }

    public void setShowTop(boolean showTop) {
        mSPUtils.save(KEY_SHOW_TOP, showTop);
    }

    public boolean isShowTop() {
        return mSPUtils.get(KEY_SHOW_TOP, true);
    }

    public void setHideAboutMe(boolean hideAboutMe) {
        mSPUtils.save(KEY_HIDE_ABOUT_ME, hideAboutMe);
    }

    public boolean isHideAboutMe() {
        return mSPUtils.get(KEY_HIDE_ABOUT_ME, false);
    }

    public void setHideOpen(boolean hideOpen) {
        mSPUtils.save(KEY_HIDE_OPEN, hideOpen);
    }

    public boolean isHideOpen() {
        return mSPUtils.get(KEY_HIDE_OPEN, false);
    }

    public void setWebSwipeBackEdge(boolean webSwipeBackEdge) {
        mSPUtils.save(KEY_WEB_SWIPE_BACK_EDGE, webSwipeBackEdge);
    }

    public boolean isWebSwipeBackEdge() {
        return mSPUtils.get(KEY_WEB_SWIPE_BACK_EDGE, false);
    }

    public void setRvAnim(int anim) {
        mSPUtils.save(KEY_RV_ANIM, anim);
    }

    public int getRvAnim() {
        return mSPUtils.get(KEY_RV_ANIM, RvAnimUtils.RvAnim.NONE);
    }

    public void setSearchHistoryMaxCount(int count) {
        mSPUtils.save(KEY_SEARCH_HISTORY_MAX_COUNT, count);
    }

    public int getSearchHistoryMaxCount() {
        return mSPUtils.get(KEY_SEARCH_HISTORY_MAX_COUNT, 20);
    }

    public void setUpdateIgnoreDuration(long dur) {
        mSPUtils.save(KEY_UPDATE_IGNORE_DURATION, dur);
    }

    public long getUpdateIgnoreDuration() {
        return mSPUtils.get(KEY_UPDATE_IGNORE_DURATION, 24 * 60 * 60 * 1000L);
    }
}
