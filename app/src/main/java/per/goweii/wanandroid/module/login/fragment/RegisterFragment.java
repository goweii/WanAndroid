package per.goweii.wanandroid.module.login.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.module.login.activity.LoginActivity;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.login.presenter.RegisterPresenter;
import per.goweii.wanandroid.module.login.view.RegisterView;
import per.goweii.wanandroid.widget.InputView;
import per.goweii.wanandroid.widget.PasswordInputView;
import per.goweii.wanandroid.widget.SubmitView;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class RegisterFragment extends BaseFragment<RegisterPresenter> implements RegisterView {

    @BindView(R.id.ll_go_login)
    LinearLayout ll_go_login;
    @BindView(R.id.piv_register_account)
    InputView piv_account;
    @BindView(R.id.piv_register_password)
    PasswordInputView piv_password;
    @BindView(R.id.piv_register_password_again)
    PasswordInputView piv_password_again;
    @BindView(R.id.sv_register)
    SubmitView sv_register;

    private LoginActivity mActivity;

    public static RegisterFragment create() {
        return new RegisterFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_register;
    }

    @Nullable
    @Override
    protected RegisterPresenter initPresenter() {
        return new RegisterPresenter();
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
        piv_password_again.setOnPwdFocusChangedListener(new PasswordInputView.OnPwdFocusChangedListener() {
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
        mActivity.getSoftInputHelper().moveWith(sv_register,
                piv_account.getEditText(), piv_password.getEditText(), piv_password_again.getEditText());
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

    @OnClick({R.id.ll_go_login, R.id.sv_register})
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected boolean onClick1(View v) {
        switch (v.getId()) {
            default:
                return false;
            case R.id.ll_go_login:
                mActivity.changeToLogin();
                break;
            case R.id.sv_register:
                String userName = piv_account.getText();
                String password = piv_password.getText();
                String repassword = piv_password_again.getText();
                presenter.register(userName, password, repassword);
                break;
        }
        return true;
    }

    @Override
    public void registerSuccess(int code, LoginBean data) {
        new LoginEvent(true).post();
        finish();
    }

    @Override
    public void registerFailed(int code, String msg) {
        ToastMaker.showShort(msg);
    }
}
