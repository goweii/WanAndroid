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

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.Random;

import butterknife.BindView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.SoftInputHelper;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.login.fragment.LoginFragment;
import per.goweii.wanandroid.module.login.fragment.RegisterFragment;
import per.goweii.wanandroid.module.login.presenter.LoginPresenter;
import per.goweii.wanandroid.widget.LogoAnimView;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class LoginActivity extends BaseActivity {

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

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.swipeback_activity_open_bottom_in,
                    R.anim.swipeback_activity_open_top_out);
        }
    }

    @Override
    protected int swipeBackDirection() {
        return SwipeBackDirection.FROM_TOP;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Nullable
    @Override
    protected LoginPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mSoftInputHelper = SoftInputHelper.attach(this).moveBy(rl_input);
        FixedFragmentPagerAdapter adapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        adapter.setFragmentList(LoginFragment.create(), RegisterFragment.create());
    }

    @Override
    protected void loadData() {
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

    private Random mRandom = new Random();

    private float[] calculateRandomXY() {
        float x = mRandom.nextInt(mMaxMoveDistanceX) - (mMaxMoveDistanceX * 0.5F);
        float y = mRandom.nextInt(mMaxMoveDistanceY) - (mMaxMoveDistanceY * 0.5F);
        return new float[]{x, y};
    }

    private long calculateDuration(float x1, float y1, float x2, float y2) {
        float distance = (float) Math.abs(Math.sqrt(Math.pow(Math.abs((x1 - x2)), 2) + Math.pow(Math.abs((y1 - y2)), 2)));
        float maxDistance = (float) Math.abs(Math.sqrt(Math.pow(mMaxMoveDistanceX, 2) + Math.pow(mMaxMoveDistanceY, 2)));
        long duration = (long) (mMaxMoveDuration * (distance / maxDistance));
        LogUtils.i("calculateDuration", "distance=" + distance + ", duration=" + duration);
        return duration;
    }
}
