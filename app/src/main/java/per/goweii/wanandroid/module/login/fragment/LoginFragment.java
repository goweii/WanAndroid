package per.goweii.wanandroid.module.login.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.module.login.activity.LoginActivity;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.login.presenter.LoginPresenter;
import per.goweii.wanandroid.module.login.view.LoginView;
import per.goweii.wanandroid.widget.InputView;
import per.goweii.wanandroid.widget.PasswordInputView;
import per.goweii.wanandroid.widget.SubmitView;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class LoginFragment extends BaseFragment<LoginPresenter> implements LoginView {

    @BindView(R.id.ll_go_register)
    LinearLayout ll_go_register;
    @BindView(R.id.piv_login_account)
    InputView piv_account;
    @BindView(R.id.piv_login_password)
    PasswordInputView piv_password;
    @BindView(R.id.sv_login)
    SubmitView sv_login;

    private LoginActivity mActivity;

    public static LoginFragment create() {
        return new LoginFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_login;
    }

    @Nullable
    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (LoginActivity) context;
    }

    @Override
    protected void initView() {
        piv_password.setOnPwdFocusChangedListener(new PasswordInputView.OnPwdFocusChangedListener() {
            @Override
            public void onFocusChanged(boolean focus) {
                mActivity.doEyeAnim(focus);
            }
        });
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.getSoftInputHelper().moveWith(sv_login,
                piv_account.getEditText(), piv_password.getEditText());
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
    }

    @Override
    public void onInvisible() {
        super.onInvisible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.ll_go_register, R.id.sv_login})
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected boolean onClick1(View v) {
        switch (v.getId()) {
            default:
                return false;
            case R.id.ll_go_register:
                mActivity.changeToRegister();
                break;
            case R.id.sv_login:
                String userName = piv_account.getText();
                String password = piv_password.getText();
                presenter.login(userName, password);
                break;
        }
        return true;
    }

    @Override
    public void loginSuccess(int code, LoginBean data) {
        new LoginEvent(true).post();
        finish();
//        AccountInfo accountInfo = new AccountInfo();
//        accountInfo.isv_refer_id = data.getId() + "";
//        accountInfo.nickname = data.getUsername();
//        CyanSdk.getInstance(getContext())
//                .setAccountInfo(accountInfo, new CallBack() {
//                    @Override
//                    public void success() {
//                        new LoginEvent(true).post();
//                        finish();
//                    }
//
//                    @Override
//                    public void error(CyanException e) {
//                        e.printStackTrace();
//                        ToastMaker.showShort(e.error_msg);
//                    }
//                });
    }

    @Override
    public void loginFailed(int code, String msg) {
        ToastMaker.showShort(msg);
    }
}
