package per.goweii.basic.core.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import per.goweii.basic.utils.ClickHelper;
import per.goweii.lazyfragment.LazyFragment;

/**
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/10-下午12:38
 */
public abstract class MvpFragment<T extends MvpPresenter> extends LazyFragment implements MvpView, View.OnClickListener {
    protected T presenter;

    /**
     * 获取布局资源文件
     *
     * @return int
     */
    @Override
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * 初始化presenter
     *
     * @return P
     */
    @Nullable
    protected abstract T initPresenter();

    /**
     * 绑定事件
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * 点击事件，可连续点击
     */
    protected boolean onClick1(final View v){
        return false;
    }

    /**
     * 点击事件，500毫秒第一次
     */
    protected void onClick2(final View v){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachPresenter();
        initialize();
    }

    @Override
    protected void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        attachPresenter();
    }

    private void attachPresenter() {
        if (presenter == null) {
            presenter = initPresenter();
        }
        if (presenter != null) {
            presenter.attach(this);
        }
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.detach();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void initialize() {
        initView();
        loadData();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    public Fragment getFragment() {
        return this;
    }

    /**
     * 用注解绑定点击事件时，在该方法绑定
     */
    @Override
    public void onClick(final View v) {
        if (!onClick1(v)) {
            ClickHelper.onlyFirstSameView(v, new ClickHelper.Callback() {
                @Override
                public void onClick(View view) {
                    onClick2(view);
                }
            });
        }
    }
}
