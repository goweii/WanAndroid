package per.goweii.basic.core.base;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import per.goweii.basic.core.mvp.MvpFragment;
import per.goweii.basic.core.utils.LoadingBarManager;
import per.goweii.basic.ui.dialog.LoadingDialog;

/**
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/10-下午12:38
 */
public abstract class BaseFragment<P extends BasePresenter> extends MvpFragment<P> {
    private LoadingDialog mLoadingDialog = null;
    private LoadingBarManager mLoadingBarManager = null;
    private Unbinder mUnbinder = null;

    /**
     * 是否注册事件分发，默认不绑定
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    protected void initialize() {
        if (getRootView() != null) {
            mUnbinder = ButterKnife.bind(this, getRootView());
        }
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        super.initialize();
    }

    @Override
    public void onDestroyView() {
        clearLoading();
        super.onDestroyView();
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
            mLoadingBarManager = LoadingBarManager.attach(getRootView());
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

    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
