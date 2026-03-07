package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class SettingChangeEvent extends BaseEvent {

    private boolean showQAChanged;
    private boolean showTopChanged;
    private boolean showBannerChanged;

    public SettingChangeEvent() {
    }

    @Override
    public void post() {
        if (showQAChanged || showTopChanged || showBannerChanged) {
            super.post();
        }
    }

    public boolean isShowQAChanged() {
        return showQAChanged;
    }

    public void setShowQAChanged(boolean showQAChanged) {
        this.showQAChanged = showQAChanged;
    }

    public boolean isShowTopChanged() {
        return showTopChanged;
    }

    public void setShowTopChanged(boolean showTopChanged) {
        this.showTopChanged = showTopChanged;
    }

    public boolean isShowBannerChanged() {
        return showBannerChanged;
    }

    public void setShowBannerChanged(boolean showBannerChanged) {
        this.showBannerChanged = showBannerChanged;
    }
}
