package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.just.agentweb.AgentWeb;

import java.util.ArrayList;
import java.util.List;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.DialogLayer;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.presenter.WebDialogPresenter;
import per.goweii.wanandroid.module.home.view.WebDialogView;
import per.goweii.wanandroid.module.main.adapter.WebDialogPagerAdapter;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebDialog extends DialogLayer implements WebDialogView {

    private final int currPos;
    private final boolean singleTipMode;

    private WebDialogPresenter presenter = null;
    private OnPageChangedListener mOnPageChangedListener = null;
    private WebDialogPagerAdapter mAdapter;

    public static WebDialog create(Context context, String url) {
        List<ArticleBean> urls = new ArrayList<>(1);
        ArticleBean bean = new ArticleBean();
        bean.setLink(url);
        urls.add(bean);
        return new WebDialog(context, urls, null, 0, true);
    }

    public static WebDialog create(Context context, final List<ArticleBean> topUrls, final List<ArticleBean> urls, final int currPos) {
        return new WebDialog(context, topUrls, urls, currPos, false);
    }

    public WebDialog(final Context context,
                     final List<ArticleBean> topUrls,
                     final List<ArticleBean> urls,
                     final int currPos,
                     final boolean singleTipMode) {
        super(context);
        this.currPos = currPos;
        this.singleTipMode = singleTipMode;
        contentView(R.layout.dialog_web);
        backgroundColorRes(R.color.dialog_bg);
        cancelableOnTouchOutside(true);
        cancelableOnClickKeyBack(true);
        onClickToDismiss(R.id.dialog_web_iv_close);
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(View target) {
                ViewPager vp = target.findViewById(R.id.dialog_web_vp);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        AnimatorHelper.createZoomAlphaInAnim(vp),
                        AnimatorHelper.createBottomInAnim(target.findViewById(R.id.dialog_web_rl_bottom_bar))
                );
                return set;
            }

            @Override
            public Animator createOutAnimator(View target) {
                ViewPager vp = target.findViewById(R.id.dialog_web_vp);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        AnimatorHelper.createZoomAlphaOutAnim(vp),
                        AnimatorHelper.createBottomOutAnim(target.findViewById(R.id.dialog_web_rl_bottom_bar))
                );
                return set;
            }
        });
        mAdapter = new WebDialogPagerAdapter(getActivity(), topUrls, urls);
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        mOnPageChangedListener = onPageChangedListener;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        presenter = new WebDialogPresenter();
        presenter.attach(this);
        final ViewPager vp = getView(R.id.dialog_web_vp);
        vp.setPageMargin((int) DisplayInfoUtils.getInstance().dp2px(10));
        final ImageView iv_back = getView(R.id.dialog_web_iv_back);
        final CollectView cv_collect = getView(R.id.dialog_web_cv_collect);
        if (singleTipMode) {
            iv_back.setVisibility(View.GONE);
            cv_collect.setVisibility(View.GONE);
        } else {
            iv_back.setVisibility(View.VISIBLE);
            cv_collect.setVisibility(View.VISIBLE);
            iv_back.setOnClickListener(new OnClickListener2() {
                @Override
                public void onClick2(View v) {
                    if (mAdapter != null) {
                        AgentWeb agentWeb = mAdapter.getAgentWeb(vp.getCurrentItem());
                        if (agentWeb != null) {
                            agentWeb.back();
                        }
                    }
                }
            });
            cv_collect.setOnClickListener(new CollectView.OnClickListener() {
                @Override
                public void onClick(CollectView v) {
                    ArticleBean data = mAdapter.getBean(vp.getCurrentItem());
                    if (!v.isChecked()) {
                        presenter.collect(data, v);
                    } else {
                        presenter.uncollect(data, v);
                    }
                }
            });
        }
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (mAdapter != null) {
                    ArticleBean data = mAdapter.getBean(i);
                    if (cv_collect.isChecked() != data.isCollect()) {
                        cv_collect.toggle();
                    }
                    if (mOnPageChangedListener != null) {
                        mOnPageChangedListener.onPageChanged(i, data);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        mAdapter.setOnDoubleClickListener(new WebDialogPagerAdapter.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(ArticleBean data) {
                if (presenter != null) {
                    presenter.collect(data, cv_collect);
                }
            }
        });
        vp.setAdapter(mAdapter);
        vp.setCurrentItem(currPos);
    }

    @Override
    public void onShow() {
        if (singleTipMode) {
            super.onShow();
            return;
        }
        final ViewPager vp = getView(R.id.dialog_web_vp);
        ValueAnimator anim = ValueAnimator.ofInt(vp.getPageMargin(), 0);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                vp.setPageMargin(value);
            }
        });
        anim.start();
    }

    @Override
    public void onPerRemove() {
        if (singleTipMode) {
            super.onPerRemove();
            return;
        }
        final ViewPager vp = getView(R.id.dialog_web_vp);
        ValueAnimator anim = ValueAnimator.ofInt(vp.getPageMargin(), (int) DisplayInfoUtils.getInstance().dp2px(10));
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(150);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                vp.setPageMargin(value);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                WebDialog.super.onPerRemove();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.start();
    }

    @Override
    public void onDetach() {
        if (mAdapter != null) {
            mAdapter.destroyAllAgentWeb();
        }
        if (presenter != null) {
            presenter.detach();
        }
        super.onDetach();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void showLoadingDialog() {
    }

    @Override
    public void dismissLoadingDialog() {
    }

    @Override
    public void showLoadingBar() {
    }

    @Override
    public void dismissLoadingBar() {
    }

    @Override
    public void clearLoading() {
    }

    public interface OnPageChangedListener {
        void onPageChanged(int pos, ArticleBean data);
    }

}
