package per.goweii.wanandroid.event;

import org.greenrobot.eventbus.EventBus;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class BaseEvent {

    public void post(){
        EventBus.getDefault().post(this);
    }

    public void postSticky() {
        EventBus.getDefault().postSticky(this);
    }

    public void removeSticky() {
        EventBus.getDefault().removeStickyEvent(this);
    }

}
