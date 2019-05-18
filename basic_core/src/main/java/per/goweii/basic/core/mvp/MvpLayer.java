package per.goweii.basic.core.mvp;

import android.content.Context;

import per.goweii.anylayer.BaseLayer;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/12/10
 */
public abstract class MvpLayer<P extends MvpPresenter> extends BaseLayer implements MvpView {

    private final Context mContext;

    protected P presenter = null;

    public MvpLayer(Context context){
        mContext = context;
    }

    protected abstract P initPresenter();

    protected abstract void initView();

    protected abstract void loadData();

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        presenter = initPresenter();
        if (presenter != null) {
            presenter.onCreate(this);
        }
        initialize();
    }

    protected void initialize() {
        initView();
        loadData();
    }

    @Override
    protected boolean onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        return super.onDestroy();
    }

    @Override
    public void showLoadingDialog() {
    }

    @Override
    public void dismissLoadingDialog() {
    }

    @Override
    public void clearLoading() {
    }

}