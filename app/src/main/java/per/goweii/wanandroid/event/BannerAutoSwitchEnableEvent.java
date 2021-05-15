package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class BannerAutoSwitchEnableEvent extends BaseEvent {
    private final boolean enable;

    public BannerAutoSwitchEnableEvent(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }
}
