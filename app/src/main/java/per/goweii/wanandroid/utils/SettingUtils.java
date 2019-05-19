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
    private static final String KEY_SHOW_TOP = "KEY_SHOW_TOP";
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

    public void setShowTop(boolean showTop) {
        mSPUtils.save(KEY_SHOW_TOP, showTop);
    }

    public boolean isShowTop() {
        return mSPUtils.get(KEY_SHOW_TOP, false);
    }

    public void saveSearchHistoryMaxCount(int count) {
        mSPUtils.save(KEY_SEARCH_HISTORY_MAX_COUNT, count);
    }

    public int getSearchHistoryMaxCount() {
        return mSPUtils.get(KEY_SEARCH_HISTORY_MAX_COUNT, 20);
    }

    public long getUpdateIgnoreDuration() {
        return mSPUtils.get(KEY_UPDATE_IGNORE_DURATION, 24 * 60 * 60 * 1000L);
    }
}
