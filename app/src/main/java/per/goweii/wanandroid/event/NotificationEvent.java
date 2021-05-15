package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class NotificationEvent extends BaseEvent {

    private final int count;

    public static void post(int count) {
        new NotificationEvent(count).post();
    }

    private NotificationEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
