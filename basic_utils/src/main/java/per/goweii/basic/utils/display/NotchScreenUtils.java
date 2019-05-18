package per.goweii.basic.utils.display;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import per.goweii.basic.utils.LogUtils;

/**
 * 描述：判断是否是刘海屏
 */
public class NotchScreenUtils {

    /**
     * 判断是否是刘海屏
     */
    public static boolean hasNotch(Activity activity) {
        return hasNotchP(activity) ||
                hasNotchXiaomi(activity) ||
                hasNotchHuawei(activity) ||
                hasNotchOPPO(activity) ||
                hasNotchVivo(activity);
    }

    /**
     * Android P 刘海屏判断
     */
    private static boolean hasNotchP(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            return windowInsets != null;
        }
        return false;
    }

    /**
     * 小米刘海屏判断.
     */
    private static boolean hasNotchXiaomi(Activity activity) {
        int result = 0;
        if ("Xiaomi".equals(Build.MANUFACTURER)) {
            try {
                ClassLoader classLoader = activity.getClassLoader();
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                Object[] params = new Object[2];
                params[0] = new String("ro.miui.notch");
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result == 1;
    }

    /**
     * 华为刘海屏判断
     *
     * @return
     */
    private static boolean hasNotchHuawei(Context context) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * VIVO刘海屏判断
     */
    private static boolean hasNotchVivo(Context context) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            return (boolean) method.invoke(FtFeature, 0x00000020);
        } catch (Exception ignore) {
            return false;
        }
    }

    private static boolean hasNotchOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
}
