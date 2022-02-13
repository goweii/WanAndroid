package per.goweii.wanandroid.module.mine.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.dialog.TipDialog;
import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.codex.encoder.CodeEncoder;
import per.goweii.codex.processor.zxing.ZXingEncodeQRCodeProcessor;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.dialog.CardShareDialog;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.mine.presenter.AboutPresenter;
import per.goweii.wanandroid.module.mine.view.AboutView;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.widget.LogoAnimView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class AboutActivity extends BaseActivity<AboutPresenter> implements AboutView {

    @BindView(R.id.abc)
    ActionBarCommon abc;
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
    protected AboutPresenter initPresenter() {
        return new AboutPresenter();
    }

    @Override
    protected void initView() {
        abc.setOnRightIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getAppDownloadUrl();
            }
        });
    }

    @Override
    protected void loadData() {
        tv_version_name.setText(String.format("%s(%d)",
                AppInfoUtils.getVersionName(), AppInfoUtils.getVersionCode()));
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

    @Override
    public void updateSuccess(int code, UpdateBean data) {
        new CardShareDialog(this, R.layout.layout_app_qrcode_share, new Function1<View, Unit>() {
            @SuppressLint("DefaultLocale")
            @Override
            public Unit invoke(View view) {
                TextView tv_name = view.findViewById(R.id.layout_app_qrcode_share_tv_name);
                TextView tv_version = view.findViewById(R.id.layout_app_qrcode_share_tv_version);
                ImageView iv_qrcode = view.findViewById(R.id.layout_app_qrcode_share_iv_qrcode);
                tv_name.setText(AppInfoUtils.getAppName());
                tv_version.setText(String.format("%s(%d)", data.getVersion_name(), data.getVersion_code()));
                new CodeEncoder(new ZXingEncodeQRCodeProcessor())
                        .encode(data.getUrl(), new Function1<Bitmap, Unit>() {
                            @Override
                            public Unit invoke(Bitmap bitmap) {
                                iv_qrcode.setImageBitmap(bitmap);
                                return null;
                            }
                        }, new Function1<Exception, Unit>() {
                            @Override
                            public Unit invoke(Exception e) {
                                return null;
                            }
                        });
                return null;
            }
        }).show();
    }

    @Override
    public void updateFailed(int code, String msg) {
    }
}
