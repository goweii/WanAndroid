package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.ui.dialog.TipDialog;
import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.ShowTopEvent;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.sc_show_top)
    SwitchCompat sc_show_top;
    @BindView(R.id.tv_has_update)
    TextView tv_has_update;
    @BindView(R.id.tv_curr_version)
    TextView tv_curr_version;
    private boolean mShowTop;

    public static void start(Context context){
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        tv_has_update.setText("");
        tv_curr_version.setText("当前版本" + AppInfoUtils.getVersionName());
        mShowTop = SettingUtils.getInstance().isShowTop();
        sc_show_top.setChecked(mShowTop);
        sc_show_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                SettingUtils.getInstance().setShowTop(isChecked);
            }
        });
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mShowTop != SettingUtils.getInstance().isShowTop()) {
            EventBus.getDefault().post(new ShowTopEvent(!mShowTop));
        }
    }

    @OnClick({
            R.id.ll_update, R.id.ll_about, R.id.ll_logout
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
            case R.id.ll_update:
                break;
            case R.id.ll_about:
                AboutWanAndroidActivity.start(getContext());
                break;
            case R.id.ll_logout:
                TipDialog.with(getContext())
                        .message("确定要退出登录吗？")
                        .onYes(new SimpleCallback<Void>() {
                            @Override
                            public void onResult(Void data) {
                                UserUtils.getInstance().logout();
                                new LoginEvent(false).post();
                                finish();
                            }
                        })
                        .show();
                break;
        }
    }
}
