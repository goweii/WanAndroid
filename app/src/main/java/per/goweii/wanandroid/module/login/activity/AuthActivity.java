package per.goweii.wanandroid.module.login.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.SoftInputHelper;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.swipeback.SwipeBackAbility;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.login.fragment.LoginFragment;
import per.goweii.wanandroid.module.login.fragment.RegisterFragment;
import per.goweii.wanandroid.module.login.model.LoginInfoEntity;
import per.goweii.wanandroid.module.login.presenter.AuthPresenter;
import per.goweii.wanandroid.module.login.view.AuthView;
import per.goweii.wanandroid.widget.LogoAnimView;
import per.goweii.wanandroid.widget.ParallaxStackLayout;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class AuthActivity extends BaseActivity<AuthPresenter> implements AuthView, SwipeBackAbility.Direction {

    private static final int REQ_CODE_OPEN_QUICK_LOGIN = 1;
    private static final int REQ_CODE_USE_QUICK_LOGIN = 2;

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.rl_input)
    ParallaxStackLayout rl_input;
    @BindView(R.id.iv_circle_1)
    ImageView iv_circle_1;
    @BindView(R.id.iv_circle_2)
    ImageView iv_circle_2;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.lav)
    LogoAnimView lav;

    private SoftInputHelper mSoftInputHelper;

    private LoginFragment loginFragment = null;
    private RegisterFragment registerFragment = null;

    public static void startPasswordLogin(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.swipeback_activity_open_bottom_in,
                    R.anim.swipeback_activity_open_alpha_out);
        }
    }

    public static void startQuickLogin(Context context) {
        QuickLoginActivity.Companion.startForUse(context, true);
    }

    @NonNull
    @Override
    public SwipeBackDirection swipeBackDirection() {
        return SwipeBackDirection.BOTTOM;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth;
    }

    @Nullable
    @Override
    protected AuthPresenter initPresenter() {
        return new AuthPresenter();
    }

    @Override
    protected void initView() {
        int pt = DisplayInfoUtils.getInstance().getStatusBarHeight() + getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        rl_input.setPadding(0, pt, 0, 0);
        mSoftInputHelper = SoftInputHelper.attach(this).moveBy(rl_input).moveWithScroll();
        FixedFragmentPagerAdapter adapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        loginFragment = LoginFragment.create();
        registerFragment = RegisterFragment.create();
        adapter.setFragmentList(loginFragment, registerFragment);
    }

    @Override
    protected void loadData() {
        //tryUseLoginByBiometric();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(
                R.anim.swipeback_activity_close_alpha_in,
                R.anim.swipeback_activity_close_bottom_out
        );
    }

    public SoftInputHelper getSoftInputHelper() {
        return mSoftInputHelper;
    }

    public void changeToRegister() {
        if (vp != null) {
            vp.setCurrentItem(1);
        }
    }

    public void changeToLogin() {
        if (vp != null) {
            vp.setCurrentItem(0);
        }
    }

    public void tryOpenLoginByBiometric(String username, String password) {
        QuickLoginActivity.Companion
                .startForOpen(this, new LoginInfoEntity(username, password), REQ_CODE_OPEN_QUICK_LOGIN);
    }

    public void tryUseLoginByBiometric() {
        QuickLoginActivity.Companion
                .startForUse(this, false, REQ_CODE_USE_QUICK_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_OPEN_QUICK_LOGIN:
                if (resultCode == RESULT_OK) {
                    ToastMaker.showShort("开启快捷登录成功");
                }
                finish();
                break;
            case REQ_CODE_USE_QUICK_LOGIN:
                if (resultCode == RESULT_OK) {
                    ToastMaker.showShort("快捷登录成功");
                    finish();
                } else {
                    ToastMaker.showShort("快捷登录失败，请使用密码登录");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        lav.randomBlink();
    }

    public void doEyeAnim(boolean close) {
        if (close) {
            lav.close(null);
        } else {
            lav.open(new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    if (lav != null) {
                        lav.randomBlink();
                    }
                    return null;
                }
            });
        }
    }
}
