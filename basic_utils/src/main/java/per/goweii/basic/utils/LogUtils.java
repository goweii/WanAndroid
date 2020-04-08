package per.goweii.basic.utils;

import android.util.Log;

import per.goweii.ponyo.log.Ponlog;

/**
 * Log输出工具类
 *
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/18-上午10:21
 */
public final class LogUtils {
    private static boolean DEBUGGABLE = BuildConfig.DEBUG;

    private static Ponlog.Logger sLogger;

    static {
        sLogger = Ponlog.INSTANCE.create();
        sLogger.setInvokeClass(LogUtils.class);
    }

    public static void v(String tag, Object msg) {
        log(Log.VERBOSE, tag, msg);
    }

    public static void d(String tag, Object msg) {
        log(Log.DEBUG, tag, msg);
    }

    public static void i(String tag, Object msg) {
        log(Log.INFO, tag, msg);
    }

    public static void w(String tag, Object msg) {
        log(Log.WARN, tag, msg);
    }

    public static void e(String tag, Object msg) {
        log(Log.ERROR, tag, msg);
    }

    public static void a(String tag, Object msg) {
        log(Log.ASSERT, tag, msg);
    }

    private static void log(int level, String tag, Object msg) {
        if (!DEBUGGABLE) return;
        switch (level) {
            case Log.VERBOSE:
                sLogger.log(Ponlog.Level.VERBOSE, tag, msg);
                break;
            case Log.DEBUG:
                sLogger.log(Ponlog.Level.DEBUG, tag, msg);
                break;
            case Log.INFO:
                sLogger.log(Ponlog.Level.INFO, tag, msg);
                break;
            case Log.WARN:
                sLogger.log(Ponlog.Level.WARN, tag, msg);
                break;
            case Log.ERROR:
                sLogger.log(Ponlog.Level.ERROR, tag, msg);
                break;
            case Log.ASSERT:
                sLogger.log(Ponlog.Level.ASSERT, tag, msg);
                break;
            default:
                break;
        }
    }
}
