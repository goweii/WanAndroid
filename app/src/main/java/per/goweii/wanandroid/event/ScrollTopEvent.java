package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public class ScrollTopEvent extends BaseEvent {

    private Class clazz;
    private int position;

    public ScrollTopEvent(Class clazz, int position) {
        this.clazz = clazz;
        this.position = position;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
