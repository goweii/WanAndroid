package per.goweii.basic.core.mvp;

import android.content.Context;

/**
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/4/4-下午1:23
 */
public abstract class MvpPresenter<V extends MvpView> {
    protected Context context;
    private V baseView;

    public void attach(V baseView) {
        this.baseView = baseView;
        context = baseView.getContext();
    }

    public void detach() {
        baseView = null;
        context = null;
    }

    public V getBaseView() {
        return baseView;
    }

    public boolean isAttach() {
        return baseView != null;
    }

    public Context getContext() {
        return context;
    }

    public void showLoadingDialog() {
        if (baseView != null) {
            baseView.showLoadingDialog();
        }
    }

    public void dismissLoadingDialog() {
        if (baseView != null) {
            baseView.dismissLoadingDialog();
        }
    }

    public void showLoadingBar() {
        if (baseView != null) {
            baseView.showLoadingBar();
        }
    }

    public void dismissLoadingBar() {
        if (baseView != null) {
            baseView.dismissLoadingBar();
        }
    }
}
