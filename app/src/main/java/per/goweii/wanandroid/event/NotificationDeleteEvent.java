package per.goweii.wanandroid.event;

import per.goweii.wanandroid.module.mine.model.NotificationBean;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class NotificationDeleteEvent extends BaseEvent {

    private final NotificationBean bean;

    public static void post(NotificationBean bean) {
        new NotificationDeleteEvent(bean).post();
    }

    private NotificationDeleteEvent(NotificationBean bean) {
        this.bean = bean;
    }

    public NotificationBean getNotificationBean() {
        return bean;
    }
}
