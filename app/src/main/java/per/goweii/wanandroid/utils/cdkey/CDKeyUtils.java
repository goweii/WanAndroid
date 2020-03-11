package per.goweii.wanandroid.utils.cdkey;

import androidx.annotation.NonNull;

import per.goweii.basic.utils.SPUtils;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2020/1/1
 * GitHub: https://github.com/goweii
 */
public class CDKeyUtils {

    private static final String SP_NAME = "cdkey";
    private static final String KEY = "cdkey";

    private static boolean isActivated = false;

    private static class Holder {
        private static final CDKeyUtils sInstance = new CDKeyUtils();
    }

    public static CDKeyUtils getInstance() {
        return Holder.sInstance;
    }

    private CDKeyUtils() {
        String cdKey = SPUtils.newInstance(SP_NAME).get(KEY, "");
        isActivated = isActiveCDKey(UserUtils.getInstance().getUserId() + "", cdKey);
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivatedCDKey(String cdKey) {
        isActivated = isActiveCDKey(UserUtils.getInstance().getUserId() + "", cdKey);
        SPUtils.newInstance(SP_NAME).save(KEY, cdKey);
    }

    @NonNull
    public static String createCDKey(@NonNull String userId) {
        CDKey cdKey = newCDKeyClass();
        if (cdKey == null) {
            return "";
        }
        return cdKey.createCDKey(userId);
    }

    public static boolean isActiveCDKey(@NonNull String userId, @NonNull String cdkey) {
        CDKey cdKey = newCDKeyClass();
        if (cdKey == null) {
            return false;
        }
        return cdKey.active(userId, cdkey);
    }

    private static CDKey newCDKeyClass() {
        try {
            Class clazz = Class.forName(BuildConfig.CDKEY_CLASS);
            return (CDKey) clazz.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
