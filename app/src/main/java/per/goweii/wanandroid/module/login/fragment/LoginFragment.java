package per.goweii.wanandroid.module.login.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.module.login.activity.LoginActivity;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.login.presenter.LoginPresenter;
import per.goweii.wanandroid.module.login.view.LoginView;
import per.goweii.wanandroid.utils.KeyboardHelper;
import per.goweii.wanandroid.widget.InputView;
import per.goweii.wanandroid.widget.SubmitView;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class LoginFragment extends BaseFragment<LoginPresenter> implements LoginView {

    @BindView(R.id.ll_go_register)
    LinearLayout ll_go_register;
    @BindView(R.id.piv_login_account)
    InputView piv_account;
    @BindView(R.id.piv_login_password)
    InputView piv_password;
    @BindView(R.id.sv_login)
    SubmitView sv_login;

    private KeyboardHelper mKeyboardHelper;
    private LoginActivity mActivity;

    public static LoginFragment create(){
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
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        mKeyboardHelper = KeyboardHelper.attach(mActivity)
                .init(mActivity.getRl_input(), sv_login, piv_account.getEditText(), piv_password.getEditText())
                .moveWithTranslation();
    }

    @Override
    public void onInvisible() {
        super.onInvisible();
        if (mKeyboardHelper != null) {
            mKeyboardHelper.detach();
            mKeyboardHelper = null;
        }
    }

    @Override
    public void onDestroyView() {
        if (mKeyboardHelper != null) {
            mKeyboardHelper.detach();
            mKeyboardHelper = null;
        }
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
    }

    @Override
    public void loginFailed(int code, String msg) {
        ToastMaker.showShort(msg);
    }
}
