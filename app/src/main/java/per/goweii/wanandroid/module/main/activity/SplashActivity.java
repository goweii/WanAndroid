package per.goweii.wanandroid.module.main.activity;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.databinding.ActivitySplashBinding;

/**
 * @author CuiZhen
 * @date 2019/5/7
 * GitHub: https://github.com/goweii
 */
public class SplashActivity extends BaseActivity {

    @Nullable
    @Override
    protected ViewBinding initViewBinding(@NonNull LayoutInflater inflater) {
//        return ActivitySplashBinding.inflate(inflater);
        return null;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        MainActivity.start(getContext());
        finish();
        overridePendingTransition(R.anim.zoom_small_in, R.anim.zoom_small_out);
    }

    @Override
    protected void loadData() {
    }
}
