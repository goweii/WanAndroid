package per.goweii.wanandroid.module.main.model;

import androidx.annotation.Nullable;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/9/28
 * GitHub: https://github.com/goweii
 */
public class ConfigBean extends BaseBean {
    @Nullable
    private List<String> enableAtDate;
    private boolean grayFilter;
    private String theme;
    private String homeTitle;
    private String actionBarBgColor;
    private String actionBarBgImageUrl;
    private String secondFloorBgImageUrl;
    private float secondFloorBgImageBlurPercent;

    public void setEnableAtDate(@Nullable List<String> enableAtDate) {
        this.enableAtDate = enableAtDate;
    }

    @Nullable
    public List<String> getEnableAtDate() {
        return enableAtDate;
    }

    public boolean isEnableAtNow() {
        List<DateRangeEntity> list = DateRangeEntity.parse(enableAtDate);
        if (list == null) {
            return true;
        }
        for (DateRangeEntity entity : list) {
            if (entity.containsNow()) {
                return true;
            }
        }
        return false;
    }

    public boolean isGrayFilter() {
        return grayFilter;
    }

    public void setGrayFilter(boolean grayFilter) {
        this.grayFilter = grayFilter;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public String getSecondFloorBgImageUrl() {
        return secondFloorBgImageUrl;
    }

    public void setSecondFloorBgImageUrl(String secondFloorBgImageUrl) {
        this.secondFloorBgImageUrl = secondFloorBgImageUrl;
    }

    public float getSecondFloorBgImageBlurPercent() {
        return secondFloorBgImageBlurPercent;
    }

    public void setSecondFloorBgImageBlurPercent(float secondFloorBgImageBlurPercent) {
        this.secondFloorBgImageBlurPercent = secondFloorBgImageBlurPercent;
    }
}
