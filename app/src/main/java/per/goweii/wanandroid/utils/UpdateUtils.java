package per.goweii.wanandroid.utils;

import java.util.Date;

import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.basic.utils.SPUtils;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public class UpdateUtils {

    private static final String SP_NAME = "update";
    private static final String KEY_VERSION_CODE = "KEY_VERSION_CODE";
    private static final String KEY_TIME = "KEY_TIME";
    private static final String KEY_LAST_CHECK_TIME = "KEY_LAST_CHECK_TIME";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    public static UpdateUtils newInstance() {
        return new UpdateUtils();
    }

    private UpdateUtils() {
    }

    public void ignore(int versionCode) {
        mSPUtils.save(KEY_VERSION_CODE, versionCode);
        mSPUtils.save(KEY_TIME, System.currentTimeMillis());
    }

    public boolean shouldUpdate(int versionCode) {
        if (!isNewest(versionCode)) {
            return false;
        }
        int ignoreCode = mSPUtils.get(KEY_VERSION_CODE, 0);
        if (versionCode > ignoreCode) {
            return true;
        }
        long currTime = System.currentTimeMillis();
        long ignoreTime = mSPUtils.get(KEY_TIME, 0L);
        long duration = SettingUtils.getInstance().getUpdateIgnoreDuration();
        return currTime - ignoreTime > duration;
    }

    public boolean isNewest(int versionCode) {
        int currCode = AppInfoUtils.getVersionCode();
        return versionCode > currCode;
    }

    public boolean isTodayChecked() {
        long last = mSPUtils.get(KEY_LAST_CHECK_TIME, 0L);
        long curr = System.currentTimeMillis();
        mSPUtils.save(KEY_LAST_CHECK_TIME, curr);
        Date lastDate = new Date(last);
        Date currDate = new Date(curr);
        return lastDate.getYear() == currDate.getYear() &&
                lastDate.getMonth() == currDate.getMonth() &&
                lastDate.getDay() == currDate.getDay();
    }
}
