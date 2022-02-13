package per.goweii.wanandroid.utils;

import java.util.Date;

import per.goweii.basic.utils.SPUtils;
import per.goweii.wanandroid.module.main.model.AdvertBean;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class ADUtils {
    private static final String SP_NAME = "ad";
    private static final String KEY_AD = "KEY_AD";

    private final SPUtils mSPUtils = SPUtils.newInstance(SP_NAME);

    private static class Holder {
        private static final ADUtils INSTANCE = new ADUtils();
    }

    public static ADUtils getInstance() {
        return Holder.INSTANCE;
    }

    private ADUtils() {
    }

    public boolean shouldShowAD(AdvertBean advertBean) {
        AdvertBean.ShowMode showMode = AdvertBean.ShowMode.from(advertBean.getShowMode());
        boolean shouldShow = false;
        switch (showMode) {
            case DO_NOT_SHOW:
                shouldShow = false;
                break;
            case ONCE_OF_DAY:
                shouldShow = !ADUtils.getInstance().isADShownToday();
                break;
            case EVERY_TIME:
                shouldShow = true;
                break;
        }
        if (shouldShow) {
            shouldShow = advertBean.shouldShowAtNow();
        }
        return shouldShow;
    }

    public boolean isADShownToday() {
        long showTime = mSPUtils.get(KEY_AD, 0L);
        long currTime = System.currentTimeMillis();
        if (currTime - showTime > 24 * 60 * 60 * 1000L) {
            return false;
        }
        Date currDate = new Date(currTime);
        Date showDate = new Date(showTime);
        if (currDate.getYear() != showDate.getYear()) {
            return false;
        }
        if (currDate.getMonth() != showDate.getMonth()) {
            return false;
        }
        if (currDate.getDay() != showDate.getDay()) {
            return false;
        }
        return true;
    }

    public void setAdShown() {
        mSPUtils.save(KEY_AD, System.currentTimeMillis());
    }
}
