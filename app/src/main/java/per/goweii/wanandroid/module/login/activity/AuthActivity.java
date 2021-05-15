package per.goweii.wanandroid.module.login.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.Random;

import butterknife.BindView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.SoftInputHelper;
import per.goweii.swipeback.SwipeBackAbility;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.login.fragment.LoginFragment;
import per.goweii.wanandroid.module.login.fragment.RegisterFragment;
import per.goweii.wanandroid.module.login.model.LoginInfoEntity;
import per.goweii.wanandroid.module.login.presenter.AuthPresenter;
import per.goweii.wanandroid.module.login.view.AuthView;
import per.goweii.wanandroid.widget.LogoAnimView;

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
    RelativeLayout rl_input;
    @BindView(R.id.iv_circle_1)
    ImageView iv_circle_1;
    @BindView(R.id.iv_circle_2)
    ImageView iv_circle_2;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.lav)
    LogoAnimView lav;

    private boolean isRunning = false;
    private AnimatorSet mSet1;
    private AnimatorSet mSet2;
    private SoftInputHelper mSoftInputHelper;

    private LoginFragment loginFragment = null;
    private RegisterFragment registerFragment = null;

    public static void startPasswordLogin(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.swipeback_activity_open_bottom_in,
                    R.anim.swipeback_activity_open_top_out);
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
        mSoftInputHelper = SoftInputHelper.attach(this).moveBy(rl_input);
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
        isRunning = true;
        mSet1 = startCircleAnim(iv_circle_1);
        mSet2 = startCircleAnim(iv_circle_2);
        super.onStart();
    }

    @Override
    protected void onStop() {
        isRunning = false;
        stopCircleAnim();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.swipeback_activity_close_top_in,
                R.anim.swipeback_activity_close_bottom_out);
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

    private void stopCircleAnim() {
        if (mSet1 != null) {
            mSet1.cancel();
            mSet1 = null;
        }
        if (mSet2 != null) {
            mSet2.cancel();
            mSet2 = null;
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

    private AnimatorSet startCircleAnim(View target) {
        if (target == null) {
            return null;
        }
        float[] xy = calculateRandomXY();
        AnimatorSet set = createTranslationAnimator(target, xy[0], xy[1]);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isRunning) {
                    startCircleAnim(target);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
        return set;
    }

    private final long mMaxMoveDuration = 10000L;
    private final int mMaxMoveDistanceX = 200;
    private final int mMaxMoveDistanceY = 20;

    private AnimatorSet createTranslationAnimator(View target, float toX, float toY) {
        float fromX = target.getTranslationX();
        float fromY = target.getTranslationY();
        long duration = calculateDuration(fromX, fromY, toX, toY);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(target, "translationX", fromX, toX);
        animatorX.setDuration(duration);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", fromY, toY);
        animatorY.setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        return set;
    }

    private final Random mRandom = new Random();

    private float[] calculateRandomXY() {
        float x = mRandom.nextInt(mMaxMoveDistanceX) - (mMaxMoveDistanceX * 0.5F);
        float y = mRandom.nextInt(mMaxMoveDistanceY) - (mMaxMoveDistanceY * 0.5F);
        return new float[]{x, y};
    }

    private long calculateDuration(float x1, float y1, float x2, float y2) {
        float distance = (float) Math.abs(Math.sqrt(Math.pow(Math.abs((x1 - x2)), 2) + Math.pow(Math.abs((y1 - y2)), 2)));
        float maxDistance = (float) Math.abs(Math.sqrt(Math.pow(mMaxMoveDistanceX, 2) + Math.pow(mMaxMoveDistanceY, 2)));
        long duration = (long) (mMaxMoveDuration * (distance / maxDistance));
        return duration;
    }
}
