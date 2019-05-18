package per.goweii.wanandroid.module.mine.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.utils.ImageLoader;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AboutMeActivity extends BaseActivity {

    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.iv_blur)
    ImageView iv_blur;
    @BindView(R.id.civ_icon)
    ImageView civ_icon;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_sign)
    TextView tv_sign;
    @BindView(R.id.ll_github)
    LinearLayout ll_github;
    @BindView(R.id.ll_jianshu)
    LinearLayout ll_jianshu;
    @BindView(R.id.tv_github)
    TextView tv_github;
    @BindView(R.id.tv_jianshu)
    TextView tv_jianshu;
    @BindView(R.id.rl_info)
    RelativeLayout rl_info;
    @BindView(R.id.rl_reward)
    RelativeLayout rl_reward;

    public static void start(Context context){
        Intent intent = new Intent(context, AboutMeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_me;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        changeVisible(View.INVISIBLE, civ_icon, tv_name, tv_sign, ll_github, ll_jianshu);
        ImageLoader.userBlur(iv_blur, R.drawable.goweii);
        iv_blur.setAlpha(0F);
        iv_blur.post(new Runnable() {
            @Override
            public void run() {
                changeViewAlpha(iv_blur, 0, 1, 600);
                changeViewSize(iv_blur, 2, 1, 1000);
            }
        });
        civ_icon.setImageResource(R.drawable.goweii);
        civ_icon.post(new Runnable() {
            @Override
            public void run() {
                changeViewSize(civ_icon, 0, 1, 300);
                doDelayShowAnim(800, 60, civ_icon, tv_name, tv_sign, ll_github, ll_jianshu);
            }
        });
        rl_info.post(new Runnable() {
            @Override
            public void run() {
                rl_info.setVisibility(View.VISIBLE);
                rl_reward.setTranslationY(rl_info.getMeasuredHeight());
                rl_reward.setVisibility(View.VISIBLE);
            }
        });
        srl.setOnMultiPurposeListener(new OnMultiPurposeListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                if (percent > 1) {
                    doTranslationYAnim(rl_info, 0);
                    doTranslationYAnim(rl_reward, rl_info.getMeasuredHeight());
                }
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {
            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
                if (percent > 1) {
                    doTranslationYAnim(rl_info, -rl_info.getMeasuredHeight());
                    doTranslationYAnim(rl_reward, 0);
                }
            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {
            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) {
            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }
        });
    }

    @Override
    protected void loadData() {
        tv_name.setText("Goweii");
        tv_sign.setText("不相信自己的人没有努力的价值");
        tv_github.setText("https://github.com/goweii");
        tv_jianshu.setText("https://www.jianshu.com/u/78fecab193fa");
    }

    @OnClick({
            R.id.ll_github, R.id.ll_jianshu
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
            case R.id.ll_github:
                WebActivity.start(getContext(), tv_name.getText().toString(), tv_github.getText().toString());
                break;
            case R.id.ll_jianshu:
                WebActivity.start(getContext(), tv_name.getText().toString(), tv_jianshu.getText().toString());
                break;
        }
    }

    private void changeVisible(int visible, View... views){
        for (View view : views) {
            view.setVisibility(visible);
        }
    }

    private void changeViewSize(final View target, float from, float to, long dur) {
        final ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(dur);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (target == null) {
                    animator.cancel();
                    return;
                }
                float f = (float) animator.getAnimatedValue();
                target.setScaleX(f);
                target.setScaleY(f);
            }
        });
        animator.start();
    }

    private void changeViewAlpha(final View target, float from, float to, long dur) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "alpha", from, to);
        animator.setDuration(dur);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void doDelayShowAnim(long dur, long delay, View... targets){
        for (int i = 0; i < targets.length; i++) {
            final View target = targets[i];
            target.setAlpha(0);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", 100, 0);
            ObjectAnimator animatorA = ObjectAnimator.ofFloat(target, "alpha", 0, 1);
            animatorY.setDuration(dur);
            animatorA.setDuration((long) (dur * 0.618F));
            AnimatorSet animator = new AnimatorSet();
            animator.playTogether(animatorA, animatorY);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setStartDelay(delay * i);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    changeVisible(View.VISIBLE, target);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }

    private void doTranslationYAnim(View target, int to){
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", target.getTranslationY(), to);
        animatorY.setDuration(500);
        animatorY.setInterpolator(new DecelerateInterpolator());
        animatorY.start();
    }
}
