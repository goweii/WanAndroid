package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.File;

import per.goweii.anypermission.AnyPermission;
import per.goweii.anypermission.RequestListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.utils.LogUtils;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.utils.ThemeUtils;

public class InstallApkActivity extends BaseActivity {
    private static final String APK_PATH = "apk_path";

    public static void start(Context context, File apk) {
        Intent intent = new Intent(context, InstallApkActivity.class);
        intent.putExtra(APK_PATH, apk.getPath());
        context.startActivity(intent);
    }

    private File apkFile;

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
        String apkPath = getIntent().getStringExtra(APK_PATH);
        if (TextUtils.isEmpty(apkPath)) {
            finish();
            return;
        }
        apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            finish();
        }
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!apkFile.exists()) {
            finish();
            return;
        }
        AnyPermission.with(this)
                .install(apkFile)
                .request(new RequestListener() {
                    @Override
                    public void onSuccess() {
                        LogUtils.d("ThemeUtils", "installApk");
                        ThemeUtils.setWillInstall();
                        if (!ThemeUtils.isDefLauncher(getApplicationContext())) {
                            WanApp.finishAllActivity();
                            ThemeUtils.resetLauncher(getApplicationContext());
                            InstallApkActivity.start(InstallApkActivity.this, apkFile);
                        }
                    }

                    @Override
                    public void onFailed() {
                        ThemeUtils.setNotInstall();
                        finish();
                    }
                });
    }
}
