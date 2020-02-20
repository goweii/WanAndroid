package per.goweii.wanandroid.module.main.model;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/9/28
 * GitHub: https://github.com/goweii
 */
public class ConfigBean extends BaseBean {
    /**
     * dailyMsg :
     * actionBarBgColor :
     * actionBarBgImageUrl :
     */

    private String dailyMsg;
    private String homeTitle;
    private String actionBarBgColor;
    private String actionBarBgImageUrl;

    public String getDailyMsg() {
        return dailyMsg;
    }

    public void setDailyMsg(String dailyMsg) {
        this.dailyMsg = dailyMsg;
    }

    public String getHomeTitle() {
        return homeTitle;
    }

    public void setHomeTitle(String homeTitle) {
        this.homeTitle = homeTitle;
    }

    public String getActionBarBgColor() {
        return actionBarBgColor;
    }

    public void setActionBarBgColor(String actionBarBgColor) {
        this.actionBarBgColor = actionBarBgColor;
    }

    public String getActionBarBgImageUrl() {
        return actionBarBgImageUrl;
    }

    public void setActionBarBgImageUrl(String actionBarBgImageUrl) {
        this.actionBarBgImageUrl = actionBarBgImageUrl;
    }
}
