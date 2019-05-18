package per.goweii.wanandroid.module.login.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.login.model.LoginBean;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface RegisterView extends BaseView {
    void registerSuccess(int code, LoginBean data);
    void registerFailed(int code, String msg);
}
