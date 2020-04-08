package per.goweii.basic.core.common;

import per.goweii.basic.core.BuildConfig;

/**
 * 可变的参数
 *
 * @author Cuizhen
 */
public final class Config {

    public static final long HTTP_TIMEOUT = 5000;

    public static final String BASE_URL = "https://www.wanandroid.com/";

    /**
     * 强制退出登陆状态的本地广播的Action
     */
    public static final String ACTION_FORCE_OFFLINE = BuildConfig.APPLICATION_ID + ".receiver.FORCE_OFFLINE";

    /**
     * 更新APP的本地广播的Action
     */
    public static final String ACTION_APP_UPDATE = BuildConfig.APPLICATION_ID + ".receiver.APP_UPDATE";

    /**
     * 软件版本更新同一版本最大忽略时间
     */
    public static final long APP_UPDATE_IGNORE_TIME_MAX_MILL = 1 * 24 * 60 * 60 * 1000;
}