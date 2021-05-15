package per.goweii.wanandroid.utils.cdkey;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private static final String KEY_USERID = "userid";
    private static final String KEY_CDKEY = "cdkey";

    private static class Holder {
        private static final CDKeyUtils sInstance = new CDKeyUtils();
    }

    public static CDKeyUtils getInstance() {
        return Holder.sInstance;
    }

    @Nullable
    private final CDKey mCDKey;

    private CDKeyUtils() {
        mCDKey = newCDKey();
    }

    public boolean isActive() {
        String id = String.valueOf(UserUtils.getInstance().getWanId());
        return check(id, get());
    }

    public void set(String cdKey) {
        String id = String.valueOf(UserUtils.getInstance().getWanId());
        SPUtils.newInstance(SP_NAME)
                .save(KEY_USERID, id)
                .save(KEY_CDKEY, cdKey);
    }

    @NonNull
    public String get() {
        if (!UserUtils.getInstance().isLogin()) {
            return "";
        }
        String currId = String.valueOf(UserUtils.getInstance().getWanId());
        String saveId = SPUtils.newInstance(SP_NAME).get(KEY_USERID, "");
        if (!TextUtils.equals(currId, saveId)) {
            return "";
        }
        return SPUtils.newInstance(SP_NAME).get(KEY_CDKEY, "");
    }

    @NonNull
    public String create(@NonNull String userId) {
        if (mCDKey == null) return "";
        return mCDKey.create(userId);
    }

    public boolean check(@NonNull String userId, @NonNull String cdkey) {
        if (mCDKey == null) return false;
        return mCDKey.check(userId, cdkey);
    }

    private CDKey newCDKey() {
        try {
            Class<?> clazz = Class.forName(BuildConfig.CDKEY_CLASS);
            return (CDKey) clazz.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
