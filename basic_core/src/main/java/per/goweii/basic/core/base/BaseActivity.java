package per.goweii.basic.core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import per.goweii.basic.core.CoreInit;
import per.goweii.basic.core.mvp.MvpActivity;
import per.goweii.basic.core.receiver.LoginReceiver;
import per.goweii.basic.core.utils.LoadingBarManager;
import per.goweii.basic.ui.dialog.LoadingDialog;
import per.goweii.basic.utils.listener.SimpleCallback;

/**
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/4/4-下午1:23
 */
public abstract class BaseActivity<P extends BasePresenter> extends MvpActivity<P> {
    private LoadingDialog mLoadingDialog = null;
    private LoadingBarManager mLoadingBarManager = null;
    private Unbinder mUnbinder = null;
    private LoginReceiver mLoginReceiver;

    /**
     * 是否注册事件分发，默认不绑定
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initialize() {
        mUnbinder = ButterKnife.bind(this);
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        super.initialize();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onResume() {
        super.onResume();
        mLoginReceiver = LoginReceiver.register(this, new SimpleCallback<Integer>() {
            @Override
            public void onResult(Integer data) {
                if (data == LoginReceiver.NOT_LOGIN) {
                    SimpleCallback<Activity> callback = CoreInit.getInstance().getOnGoLoginCallback();
                    if (callback != null) {
                        callback.onResult(getActivity());
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoginReceiver != null) {
            mLoginReceiver.unregister();
        }
    }

    @Override
    protected void onDestroy() {
        clearLoading();
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.with(getContext());
        }
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showLoadingBar() {
        if (mLoadingBarManager == null) {
            mLoadingBarManager = LoadingBarManager.attach(getWindow().getDecorView());
        }
        mLoadingBarManager.show();
    }

    @Override
    public void dismissLoadingBar() {
        if (mLoadingBarManager != null) {
            mLoadingBarManager.dismiss();
        }
    }

    @Override
    public void clearLoading() {
        if (mLoadingBarManager != null) {
            mLoadingBarManager.clear();
            mLoadingBarManager.detach();
        }
        mLoadingBarManager = null;
        if (mLoadingDialog != null) {
            mLoadingDialog.clear();
        }
        mLoadingDialog = null;
    }

}
