package per.goweii.wanandroid.module.login.presenter;

import com.sohu.cyan.android.sdk.api.CallBack;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.entity.AccountInfo;
import com.sohu.cyan.android.sdk.exception.CyanException;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.login.model.LoginRequest;
import per.goweii.wanandroid.module.login.view.LoginView;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    public void login(String userName, String password){
        addToRxLife(LoginRequest.login(userName, password, new RequestListener<LoginBean>() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(int code, LoginBean data) {
                loginCyan(code, data);
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().loginFailed(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {

            }

            @Override
            public void onFinish() {
                dismissLoadingDialog();
            }
        }));
    }

    private void loginCyan(int code, LoginBean data) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.isv_refer_id = data.getId() + "";
        accountInfo.nickname = data.getUsername();
        CyanSdk.getInstance(getContext())
                .setAccountInfo(accountInfo, new CallBack() {
                    @Override
                    public void success() {
                        UserUtils.getInstance().login(data);
                        if (isAttach()) {
                            getBaseView().loginSuccess(code, data);
                        }
                    }

                    @Override
                    public void error(CyanException e) {
                        e.printStackTrace();
                        if (isAttach()) {
                            getBaseView().loginFailed(WanApi.ApiCode.ERROR, e.error_msg);
                        }
                    }
                });
    }
}
