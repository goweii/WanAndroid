package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;

public class BringToFrontActivity extends BaseActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, BringToFrontActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        finish();
    }

    @Override
    protected void loadData() {
    }
}
