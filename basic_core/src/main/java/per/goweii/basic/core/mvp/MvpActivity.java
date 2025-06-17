package per.goweii.basic.core.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import per.goweii.basic.utils.ClickHelper;


/**
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/4/4-下午1:23
 */
public abstract class MvpActivity<P extends MvpPresenter, V extends ViewBinding> extends CacheActivity implements MvpView, View.OnClickListener {
    public P presenter;
    public V binding;

    /**
     * 初始化ViewBinding
     *
     * @return V
     */
    @Nullable
    protected abstract V initViewBinding(@NonNull LayoutInflater inflater);

    /**
     * 初始化presenter
     */
    @Nullable
    protected abstract P initPresenter();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 绑定数据
     */
    protected abstract void loadData();

    /**
     * 点击事件，可连续点击
     */
    protected boolean onClick1(final View v) {
        return false;
    }

    /**
     * 点击事件，500毫秒第一次
     */
    protected void onClick2(final View v) {
    }

    protected void initWindow() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        binding = initViewBinding(getLayoutInflater());
        if (binding != null) {
            setContentView(binding.getRoot());
        }
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attach(this);
        }
        initialize();
    }

    protected void initialize() {
        initView();
        loadData();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.detach();
        }
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    protected Activity getActivity() {
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
