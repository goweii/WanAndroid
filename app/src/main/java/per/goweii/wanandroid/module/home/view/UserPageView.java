package per.goweii.wanandroid.module.home.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.UserPageBean;

/**
 * @author CuiZhen
 * @date 2019/10/3
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface UserPageView extends BaseView {
    void getUserPageSuccess(int code, UserPageBean data);

    void getUserPageFailed(int code, String msg);
}
