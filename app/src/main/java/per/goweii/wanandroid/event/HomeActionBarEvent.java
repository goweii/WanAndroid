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
    private String secondFloorBg;
    private float secondFloorBgBlurPercent;

    public HomeActionBarEvent(String text,
                              String color,
                              String image,
                              String secondFloorBg,
                              float secondFloorBgBlurPercent) {
        this.text = text;
        this.color = color;
        this.image = image;
        this.secondFloorBg = secondFloorBg;
        this.secondFloorBgBlurPercent = secondFloorBgBlurPercent;
    }

    public HomeActionBarEvent() {
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

    public String getSecondFloorBgImageUrl() {
        return secondFloorBg;
    }

    public float getSecondFloorBgImageBlurPercent() {
        return secondFloorBgBlurPercent;
    }
}
