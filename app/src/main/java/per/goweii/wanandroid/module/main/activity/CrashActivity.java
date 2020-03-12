package per.goweii.wanandroid.module.main.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.statusbarcompat.StatusBarCompat;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;

/**
 * @author CuiZhen
 * @date 2019/10/20
 * GitHub: https://github.com/goweii
 */
public class CrashActivity extends AppCompatActivity {

    @BindView(R.id.tv_log)
    TextView tv_log;
    @BindView(R.id.tv_show_log)
    TextView tv_show_log;
    @BindView(R.id.iv_bug)
    ImageView iv_bug;
    @BindView(R.id.sv)
    ScrollView sv;

    private Unbinder mUnbinder = null;
    private CaocConfig mCaocConfig;

    private boolean isLogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCaocConfig = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        if (mCaocConfig == null) {
            finish();
            return;
        }
        WanApp.initDarkMode();
        StatusBarCompat.setIconMode(this, !WanApp.isDarkMode());
        setContentView(R.layout.activity_crash);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @OnClick({R.id.tv_restart, R.id.tv_exit, R.id.tv_show_log})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_show_log:
                String log = tv_log.getText().toString();
                if (TextUtils.isEmpty(log)) {
                    isLogShown = true;
                    tv_show_log.setText("复制日志");
                    tv_show_log.setTextColor(getResources().getColor(R.color.text_main));
                    iv_bug.setVisibility(View.GONE);
                    sv.setVisibility(View.VISIBLE);
                    tv_log.setText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent()));
                } else {
                    CopyUtils.copyText(log);
                    tv_show_log.setText("日志已复制");
                    tv_show_log.setTextColor(getResources().getColor(R.color.text_third));
                    tv_show_log.setEnabled(false);
                }
                break;
            case R.id.tv_exit:
                CustomActivityOnCrash.closeApplication(this, mCaocConfig);
                break;
            case R.id.tv_restart:
                CustomActivityOnCrash.restartApplication(this, mCaocConfig);
                break;
        }
    }
}
