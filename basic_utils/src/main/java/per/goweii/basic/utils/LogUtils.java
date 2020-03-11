package per.goweii.basic.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * Log输出工具类
 *
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/18-上午10:21
 */
public final class LogUtils {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 1 << 1;
    public static final int INFO = 1 << 2;
    public static final int WARN = 1 << 3;
    public static final int ERROR = 1 << 4;
    public static final int NONE = 0;
    public static final int ALL = VERBOSE | DEBUG | INFO | WARN | ERROR;
    public static final int DEBUG_ABOVE = DEBUG | INFO | WARN | ERROR;
    public static final int INFO_ABOVE = INFO | WARN | ERROR;
    public static final int WARN_ABOVE = WARN | ERROR;

    @Level
    private static int FILTER = ALL;
    private static boolean DEBUGGABLE = false;

    public static void setFilter(@Level int filter) {
        LogUtils.FILTER = filter;
    }

    public static void init(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            DEBUGGABLE = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignore) {
        }
    }

    public static void v(String tag, String msg) {
        log(tag, msg, VERBOSE);
    }

    public static void v(String tag, Object msg) {
        log(tag, msg, VERBOSE);
    }

    public static void v(String tag, Object[] msg) {
        v(tag, Arrays.toString(msg));
    }

    public static void d(String tag, String msg) {
        log(tag, msg, DEBUG);
    }

    public static void d(String tag, Object msg) {
        log(tag, msg, DEBUG);
    }

    public static void d(String tag, Object[] msg) {
        d(tag, Arrays.toString(msg));
    }

    public static void i(String tag, String msg) {
        log(tag, msg, INFO);
    }

    public static void i(String tag, Object msg) {
        log(tag, msg, INFO);
    }

    public static void i(String tag, Object[] msg) {
        i(tag, Arrays.toString(msg));
    }

    public static void w(String tag, String msg) {
        log(tag, msg, WARN);
    }

    public static void w(String tag, Object msg) {
        log(tag, msg, WARN);
    }

    public static void w(String tag, Object[] msg) {
        w(tag, Arrays.toString(msg));
    }

    public static void e(String tag, String msg) {
        log(tag, msg, ERROR);
    }

    public static void e(String tag, Object msg) {
        log(tag, msg, ERROR);
    }

    public static void e(String tag, Object[] msg) {
        e(tag, Arrays.toString(msg));
    }

    public static void log(String tag, Object msg, int filter) {
        if (msg instanceof char[]) {
            log(tag, Arrays.toString((char[]) msg), filter);
        } else if (msg instanceof byte[]) {
            log(tag, Arrays.toString((byte[]) msg), filter);
        } else if (msg instanceof short[]) {
            log(tag, Arrays.toString((short[]) msg), filter);
        } else if (msg instanceof int[]) {
            log(tag, Arrays.toString((int[]) msg), filter);
        } else if (msg instanceof long[]) {
            log(tag, Arrays.toString((long[]) msg), filter);
        } else if (msg instanceof float[]) {
            log(tag, Arrays.toString((float[]) msg), filter);
        } else if (msg instanceof double[]) {
            log(tag, Arrays.toString((double[]) msg), filter);
        } else if (msg instanceof boolean[]) {
            log(tag, Arrays.toString((boolean[]) msg), filter);
        } else {
            log(tag, String.valueOf(msg), filter);
        }
    }

    private static void log(String tag, String msg, int filter) {
        if (!DEBUGGABLE) {
            return;
        }
        switch (FILTER & filter) {
            case VERBOSE:
                Log.v(tag, msg);
                break;
            case DEBUG:
                Log.d(tag, msg);
                break;
            case INFO:
                Log.i(tag, msg);
                break;
            case WARN:
                Log.w(tag, msg);
                break;
            case ERROR:
                Log.e(tag, msg);
                break;
            default:
                break;
        }
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, NONE, ALL, DEBUG_ABOVE, INFO_ABOVE, WARN_ABOVE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Level {
    }
}
