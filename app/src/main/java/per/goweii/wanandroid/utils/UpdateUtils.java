package per.goweii.wanandroid.utils;

import java.util.Date;

import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.basic.utils.SPUtils;
import per.goweii.wanandroid.module.main.model.UpdateBean;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public class UpdateUtils {
    private static final String SP_NAME = "update";
    private static final String KEY_LAST_CHECK_TIME = "KEY_LAST_CHECK_TIME";
    private static final String KEY_VERSION_CODE = "KEY_VERSION_CODE";
    private static final String KEY_TIME = "KEY_TIME";
    private static final String KEY_BETA_VERSION_CODE = "KEY_BETA_VERSION_CODE";
    private static final String KEY_BETA_VERSION_NAME = "KEY_BETA_VERSION_NAME";
    private static final String KEY_BETA_TIME = "KEY_TEST_TIME";

    private static final String BETA = "beta";

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

    public boolean shouldUpdate(UpdateBean updateBean) {
        return shouldUpdate(updateBean.getVersion_code(), updateBean.getVersion_name());
    }

    public boolean shouldUpdate(int versionCode, String versionName) {
        if (!isNewest(versionCode, versionName)) return false;
        int ignoreCode = mSPUtils.get(KEY_VERSION_CODE, 0);
        if (versionCode > ignoreCode) return true;
        long currTime = System.currentTimeMillis();
        long ignoreTime = mSPUtils.get(KEY_TIME, 0L);
        long duration = SettingUtils.getInstance().getUpdateIgnoreDuration();
        return currTime - ignoreTime > duration;
    }

    public boolean shouldForceUpdate(UpdateBean updateBean) {
        return shouldForceUpdate(
                updateBean.isForce(),
                updateBean.getVersion_code(), updateBean.getVersion_name(),
                updateBean.getLast_force_version_code(), updateBean.getLast_force_version_name()
        );
    }

    public boolean shouldForceUpdate(
            boolean netCurrIsForce,
            int netCurrVersionCode, String netCurrVersionName,
            int netLastForceVersionCode, String netLastForceVersionName
    ) {
        boolean isLastForceNewest = isNewest(netLastForceVersionCode, netLastForceVersionName);
        if (isLastForceNewest) return true;
        boolean isCurrNewest = isNewest(netCurrVersionCode, netCurrVersionName);
        if (isCurrNewest) return netCurrIsForce;
        return false;
    }

    public void ignoreBeta(String versionName, int versionCode) {
        mSPUtils.save(KEY_BETA_VERSION_NAME, versionName);
        mSPUtils.save(KEY_BETA_VERSION_CODE, versionCode);
        mSPUtils.save(KEY_BETA_TIME, System.currentTimeMillis());
    }

    public boolean shouldUpdateBeta(UpdateBean updateBean) {
        return shouldUpdateBeta(updateBean.getVersion_code(), updateBean.getVersion_name());
    }

    public boolean shouldUpdateBeta(int versionCode, String versionName) {
        if (!isNewest(versionCode, versionName)) return false;
        int ignoreCode = mSPUtils.get(KEY_BETA_VERSION_CODE, 0);
        if (versionCode > ignoreCode) return true;
        String ignoreName = mSPUtils.get(KEY_BETA_VERSION_NAME, "");
        int ignoreBetaCode = getBetaCode(ignoreName);
        int betaCode = getBetaCode(versionName);
        if (betaCode > ignoreBetaCode) return true;
        long currTime = System.currentTimeMillis();
        long ignoreTime = mSPUtils.get(KEY_BETA_TIME, 0L);
        long duration = SettingUtils.getInstance().getUpdateIgnoreDuration();
        return currTime - ignoreTime > duration;
    }

    public boolean isNewest(UpdateBean updateBean) {
        int localCode = AppInfoUtils.getVersionCode();
        String localName = AppInfoUtils.getVersionName();
        return isNewest(localCode, localName, updateBean.getVersion_code(), updateBean.getVersion_name());
    }

    public boolean isNewest(int netCode, String netName) {
        int localCode = AppInfoUtils.getVersionCode();
        String localName = AppInfoUtils.getVersionName();
        return isNewest(localCode, localName, netCode, netName);
    }

    public boolean isNewest(int oldCode, String oldName, int newCode, String newName) {
        if (newCode > oldCode) return true;
        if (newCode < oldCode) return false;
        boolean localBeta = oldName.contains(BETA);
        boolean netBeta = newName.contains(BETA);
        if (!localBeta) return false;
        if (!netBeta) return true;
        int localBetaCode = getBetaCode(oldName);
        int netBetaCode = getBetaCode(newName);
        return netBetaCode > localBetaCode;
    }

    private int getBetaCode(String versionName) {
        int betaCode = 0;
        if (versionName.contains(BETA)) {
            String[] betaSub = versionName.split(BETA);
            if (betaSub.length == 2) {
                try {
                    betaCode = Integer.parseInt(betaSub[1]);
                } catch (Exception ignore) {
                }
            }
        }
        return betaCode;
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
