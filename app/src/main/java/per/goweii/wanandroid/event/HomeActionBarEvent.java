package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class HomeActionBarEvent extends BaseEvent {
    private String text;
    private String color;
    private String image;

    public HomeActionBarEvent(String text, String color, String image) {
        this.text = text;
        this.color = color;
        this.image = image;
    }

    public String getHomeTitle() {
        return text;
    }

    public String getActionBarBgColor() {
        return color;
    }

    public String getActionBarBgImageUrl() {
        return image;
    }
}
