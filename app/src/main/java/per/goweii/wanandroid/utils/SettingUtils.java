package per.goweii.wanandroid.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import per.goweii.basic.utils.SPUtils;
import per.goweii.wanandroid.module.explore.DailyNewsPlatform;
import per.goweii.wanandroid.module.mine.model.HostEntity;
import per.goweii.wanandroid.utils.ai.DeepSeek;
import per.goweii.wanandroid.utils.web.HostInterceptUtils;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class SettingUtils {

    private static final String SP_NAME = "setting";
    private static final String KEY_THEME_MODE = "KEY_THEME_MODE";
    @Deprecated
    private static final String KEY_SYSTEM_THEME = "KEY_SYSTEM_THEME";
    @Deprecated
    private static final String KEY_DARK_THEME = "KEY_DARK_THEME";
    private static final String KEY_SHOW_READ_LATER = "KEY_SHOW_READ_LATER";
    private static final String KEY_SHOW_READ_LATER_NOTIFICATION = "KEY_SHOW_READ_LATER_NOTIFICATION";
    private static final String KEY_SHOW_READ_RECORD = "KEY_SHOW_READ_RECORD";
    private static final String KEY_SHOW_QA = "KEY_SHOW_QA";
    private static final String KEY_SHOW_TOP = "KEY_SHOW_TOP";
    private static final String KEY_SHOW_BANNER = "KEY_SHOW_BANNER";
    private static final String KEY_HIDE_ABOUT_ME = "KEY_HIDE_ABOUT_ME";
    private static final String KEY_HIDE_OPEN = "KEY_HIDE_OPEN";
    private static final String KEY_WEB_SWIPE_BACK_EDGE = "KEY_WEB_SWIPE_BACK_EDGE";
    private static final String KEY_RV_ANIM = "KEY_RV_ANIM";
    private static final String KEY_URL_INTERCEPT_TYPE = "KEY_URL_INTERCEPT_TYPE";
    private static final String KEY_HOST_WHITE = "KEY_HOST_WHITE";
    private static final String KEY_HOST_BLACK = "KEY_HOST_BLACK";
    private static final String KEY_SEARCH_HISTORY_MAX_COUNT = "KEY_SEARCH_HISTORY_MAX_COUNT";
    private static final String KEY_UPDATE_IGNORE_DURATION = "KEY_UPDATE_IGNORE_DURATION";
    private static final String KEY_DAILY_NEWS_PLATFORM = "KEY_DAILY_NEWS_PLATFORM";
    private static final String KEY_AI_ENABLED = "KEY_AI_ENABLED";
    private static final String KEY_AI_PROVIDER = "KEY_AI_PROVIDER";
    private static final String KEY_AI_MODEL = "KEY_AI_MODEL";
    private static final String KEY_AI_API_KEY = "KEY_AI_API_KEY";

    public enum ThemeMode {
        FOLLOW_SYSTEM(0), LIGHT(1), DARK(2),
        ;

        public final int value;

        ThemeMode(int value) {
            this.value = value;
        }

        @NonNull
        public static ThemeMode fromValue(int value) {
            for (ThemeMode themeMode : values()) {
                if (themeMode.value == value) {
                    return themeMode;
                }
            }
            return ThemeMode.FOLLOW_SYSTEM;
        }
    }

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private ThemeMode mThemeMode = ThemeMode.FOLLOW_SYSTEM;
    private boolean mShowReadLaterNotification = true;
    private boolean mShowQA = false;
    private boolean mShowTop = true;
    private boolean mShowBanner = true;
    private int mUrlInterceptType = HostInterceptUtils.TYPE_NOTHING;
    private final List<HostEntity> mHostWhite = new ArrayList<>();
    private final List<HostEntity> mHostBlack = new ArrayList<>();
    private int mSearchHistoryMaxCount = 100;
    private long mUpdateIgnoreDuration = 1 * 24 * 60 * 60 * 1000L;
    private DailyNewsPlatform mDailyNewsPlatform = DailyNewsPlatform.JUEJIN;
    private boolean mAiEnabled = false;
    private String mAiProvider = DeepSeek.INSTANCE.getId();
    private String mAiModel = DeepSeek.INSTANCE.getModels().get(0).getId();
    private String mAiApiKey = "";

    private static class Holder {
        private static final SettingUtils INSTANCE = new SettingUtils();
    }

    public static SettingUtils getInstance() {
        return Holder.INSTANCE;
    }

    private SettingUtils() {
        if (mSPUtils.has(KEY_THEME_MODE)) {
            mThemeMode = ThemeMode.fromValue(mSPUtils.get(KEY_THEME_MODE, mThemeMode.value));
        } else {
            if (mSPUtils.get(KEY_SYSTEM_THEME, false)) {
                mThemeMode = ThemeMode.FOLLOW_SYSTEM;
            } else if (mSPUtils.get(KEY_DARK_THEME, false)) {
                mThemeMode = ThemeMode.DARK;
            } else {
                mThemeMode = ThemeMode.LIGHT;
            }
        }
        mShowReadLaterNotification = mSPUtils.get(KEY_SHOW_READ_LATER_NOTIFICATION, mShowReadLaterNotification);
        mShowQA = mSPUtils.get(KEY_SHOW_QA, mShowQA);
        mShowTop = mSPUtils.get(KEY_SHOW_TOP, mShowTop);
        mShowBanner = mSPUtils.get(KEY_SHOW_BANNER, mShowBanner);
        mUrlInterceptType = mSPUtils.get(KEY_URL_INTERCEPT_TYPE, mUrlInterceptType);
        mSearchHistoryMaxCount = mSPUtils.get(KEY_SEARCH_HISTORY_MAX_COUNT, mSearchHistoryMaxCount);
        mUpdateIgnoreDuration = mSPUtils.get(KEY_UPDATE_IGNORE_DURATION, mUpdateIgnoreDuration);
        mDailyNewsPlatform = DailyNewsPlatform.fromCode(mSPUtils.get(KEY_DAILY_NEWS_PLATFORM, mDailyNewsPlatform.getPlatformCode()));
        mAiEnabled = mSPUtils.get(KEY_AI_ENABLED, mAiEnabled);
        mAiProvider = mSPUtils.get(KEY_AI_PROVIDER, mAiProvider);
        mAiModel = mSPUtils.get(KEY_AI_MODEL, mAiModel);
        mAiApiKey = mSPUtils.get(KEY_AI_API_KEY, mAiApiKey);
        Gson gson = new Gson();
        String jsonWhite = mSPUtils.get(KEY_HOST_WHITE, "");
        try {
            List<HostEntity> list = gson.fromJson(jsonWhite, new TypeToken<List<HostEntity>>() {
            }.getType());
            mHostWhite.addAll(list);
        } catch (Exception e) {
            for (String host : HostInterceptUtils.WHITE_HOST) {
                mHostWhite.add(new HostEntity(host, false));
            }
            mSPUtils.save(KEY_HOST_WHITE, gson.toJson(mHostWhite));
        }
        String jsonBlack = mSPUtils.get(KEY_HOST_BLACK, "");
        try {
            List<HostEntity> list = gson.fromJson(jsonBlack, new TypeToken<List<HostEntity>>() {
            }.getType());
            mHostBlack.addAll(list);
        } catch (Exception e) {
            for (String host : HostInterceptUtils.BLACK_HOST) {
                mHostBlack.add(new HostEntity(host, false));
            }
            mSPUtils.save(KEY_HOST_BLACK, gson.toJson(mHostBlack));
        }
    }

    public void setThemeMode(@NonNull ThemeMode themeMode) {
        mThemeMode = themeMode;
        mSPUtils.save(KEY_THEME_MODE, themeMode.value);
    }

    public ThemeMode getThemeMode() {
        return mThemeMode;
    }

    public void setShowReadLaterNotification(boolean showReadLaterNotification) {
        mShowReadLaterNotification = showReadLaterNotification;
        mSPUtils.save(KEY_SHOW_READ_LATER_NOTIFICATION, showReadLaterNotification);
    }

    public boolean isShowReadLaterNotification() {
        return mShowReadLaterNotification;
    }

    public void setShowTop(boolean showTop) {
        mShowTop = showTop;
        mSPUtils.save(KEY_SHOW_TOP, showTop);
    }

    public boolean isShowTop() {
        return mShowTop;
    }

    public void setShowQA(boolean showQA) {
        mShowQA = showQA;
        mSPUtils.save(KEY_SHOW_QA, showQA);
    }

    public boolean isShowQA() {
        return mShowQA;
    }

    public void setShowBanner(boolean showBanner) {
        mShowBanner = showBanner;
        mSPUtils.save(KEY_SHOW_BANNER, showBanner);
    }

    public boolean isShowBanner() {
        return mShowBanner;
    }

    public void setUrlInterceptType(int type) {
        mUrlInterceptType = type;
        mSPUtils.save(KEY_URL_INTERCEPT_TYPE, type);
    }

    public int getUrlInterceptType() {
        return mUrlInterceptType;
    }

    public void setHostWhiteIntercept(@NonNull List<HostEntity> hosts) {
        mHostWhite.clear();
        mHostWhite.addAll(hosts);
        mSPUtils.save(KEY_HOST_WHITE, new Gson().toJson(mHostWhite));
    }

    @NonNull
    public List<HostEntity> getHostWhiteIntercept() {
        return mHostWhite;
    }

    public void setHostBlackIntercept(@NonNull List<HostEntity> hosts) {
        mHostBlack.clear();
        mHostBlack.addAll(hosts);
        mSPUtils.save(KEY_HOST_BLACK, new Gson().toJson(mHostBlack));
    }

    @NonNull
    public List<HostEntity> getHostBlackIntercept() {
        return mHostBlack;
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

    public void setDailyNewsPlatform(@NonNull DailyNewsPlatform platform) {
        mDailyNewsPlatform = platform;
        mSPUtils.save(KEY_DAILY_NEWS_PLATFORM, platform.getPlatformCode());
    }

    public DailyNewsPlatform getDailyNewsPlatform() {
        return mDailyNewsPlatform;
    }

    public void setAiEnabled(boolean enabled) {
        mAiEnabled = enabled;
        mSPUtils.save(KEY_AI_ENABLED, enabled);
    }

    public boolean isAiEnabled() {
        return mAiEnabled;
    }

    public void setAiProvider(String aiProvider) {
        mAiProvider = aiProvider;
        mSPUtils.save(KEY_AI_PROVIDER, aiProvider);
    }

    public String getAiProvider() {
        return mAiProvider;
    }

    public void setAiModel(String aiModel) {
        mAiModel = aiModel;
        mSPUtils.save(KEY_AI_MODEL, aiModel);
    }

    public String getAiModel() {
        return mAiModel;
    }

    public void setAiApiKey(String aiApiKey) {
        mAiApiKey = aiApiKey;
        mSPUtils.save(KEY_AI_API_KEY, aiApiKey);
    }

    public String getAiApiKey() {
        return mAiApiKey;
    }
}
