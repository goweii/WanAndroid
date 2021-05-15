package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.ui.dialog.TipDialog;
import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.widget.LogoAnimView;

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
    @BindView(R.id.lav)
    LogoAnimView lav;

    public static void start(Context context) {
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
        tv_version_name.setText(String.format("%s(%d)",
                AppInfoUtils.getVersionName(), AppInfoUtils.getVersionCode()));
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        lav.randomBlink();
    }

    @OnClick({
            R.id.ll_web, R.id.ll_about, R.id.ll_github, R.id.ll_beta
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
                UrlOpenUtils.Companion
                        .with("https://www.wanandroid.com")
                        .title(tv_web.getText().toString())
                        .open(getContext());
                break;
            case R.id.ll_about:
                UrlOpenUtils.Companion
                        .with("https://www.wanandroid.com/about")
                        .title(tv_about.getText().toString())
                        .open(getContext());
                break;
            case R.id.ll_github:
                UrlOpenUtils.Companion
                        .with("https://github.com/goweii/WanAndroid")
                        .title(tv_github.getText().toString())
                        .open(getContext());
                break;
            case R.id.ll_beta:
                String msg = new StringBuilder()
                        .append("需要申请开通内测更新的小伙伴，")
                        .append("请加群（见关于作者）说明内测更新并注明玩友号（见个人资料）。")
                        .append("\n")
                        .append("\n")
                        .append("注：内测更新须提前登录并更新到最新正式版")
                        .toString();
                TipDialog.with(this)
                        .title("申请内测")
                        .message(msg)
                        .msgCenter(false)
                        .yesText("知道了")
                        .singleYesBtn()
                        .show();
                break;
        }
    }
}
