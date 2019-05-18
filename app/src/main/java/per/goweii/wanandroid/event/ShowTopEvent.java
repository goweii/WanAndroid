package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ShowTopEvent extends BaseEvent {

    private boolean showTop;

    public ShowTopEvent(boolean showTop) {
        this.showTop = showTop;
    }

    public void setShowTop(boolean showTop) {
        this.showTop = showTop;
    }

    public boolean isShowTop() {
        return showTop;
    }
}
