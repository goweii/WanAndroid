package per.goweii.wanandroid.module.login.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.login.model.LoginRequest;
import per.goweii.wanandroid.module.login.view.RegisterView;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class RegisterPresenter extends BasePresenter<RegisterView> {

    public void register(
            String username,
            String password,
            String repassword
    ) {
        addToRxLife(LoginRequest.register(username, password, repassword, new RequestListener<LoginBean>() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(int code, LoginBean data) {
                UserUtils.getInstance().login(data);
                if (isAttach()) {
                    getBaseView().registerSuccess(0, UserUtils.getInstance().getLoginUser(), username, password);
                }
                dismissLoadingDialog();
            }

            @Override
            public void onFailed(int code, String msg) {
                UserUtils.getInstance().logout();
                if (isAttach()) {
                    getBaseView().registerFailed(code, msg);
                }
                dismissLoadingDialog();
            }

            @Override
            public void onError(ExceptionHandle handle) {

            }

            @Override
            public void onFinish() {
            }
        }));
    }
}
