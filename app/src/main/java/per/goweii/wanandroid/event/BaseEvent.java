package per.goweii.wanandroid.event;

import org.greenrobot.eventbus.EventBus;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class BaseEvent {

    public void post(){
        EventBus.getDefault().post(this);
    }

}
