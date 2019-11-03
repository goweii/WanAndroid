package per.goweii.basic.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * 描述：敏感词过滤
 *
 * @author Cuizhen
 * @date 2018/8/28-上午10:04
 */
public class AppInfoUtils {

    /**
     * 获取本地软件版本号code
     */
    public static int getVersionCode() {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = Utils.getAppContext().getPackageManager().getPackageInfo(Utils.getAppContext().getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号name
     */
    public static String getVersionName() {
        String versionName = "";
        try {
            PackageInfo packageInfo = Utils.getAppContext().getApplicationContext().getPackageManager().getPackageInfo(Utils.getAppContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 判断应用是否已安装
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        return getAppInfo(context, packageName) != null;
    }

    public static ApplicationInfo getAppInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static class PackageName {
        public static final String QQ = "com.tencent.mobileqq";
        public static final String WECHAT = "com.tencent.mm";
    }
}
