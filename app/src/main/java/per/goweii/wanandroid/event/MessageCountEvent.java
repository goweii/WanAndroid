package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class MessageCountEvent extends BaseEvent {

    private int count;

    public static void post(int count) {
        new MessageCountEvent(count).post();
    }

    private MessageCountEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
