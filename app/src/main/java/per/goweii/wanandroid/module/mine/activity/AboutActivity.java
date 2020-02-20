package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.WebActivity;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_version_name)
    TextView tv_version_name;
    @BindView(R.id.tv_web)
    TextView tv_web;
    @BindView(R.id.tv_about)
    TextView tv_about;
    @BindView(R.id.tv_github)
    TextView tv_github;

    public static void start(Context context){
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        tv_version_name.setText(String.format("V%s(%d)",
                AppInfoUtils.getVersionName(), AppInfoUtils.getVersionCode()));
    }

    @Override
    protected void loadData() {
    }

    @OnClick({
            R.id.ll_web, R.id.ll_about, R.id.ll_github
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onClick2(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.ll_web:
                WebActivity.start(getContext(), tv_web.getText().toString(), "https://www.wanandroid.com");
                break;
            case R.id.ll_about:
                WebActivity.start(getContext(), tv_about.getText().toString(), "https://www.wanandroid.com/about");
                break;
            case R.id.ll_github:
                WebActivity.start(getContext(), tv_github.getText().toString(), "https://github.com/goweii/WanAndroid");
                break;
        }
    }
}
