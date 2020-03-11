package per.goweii.wanandroid.module.login.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.login.model.LoginBean;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public interface LoginView extends BaseView {
    void loginSuccess(int code, LoginBean data);
    void loginFailed(int code, String msg);
}
