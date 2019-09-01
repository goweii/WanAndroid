package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;

import java.util.List;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.DialogLayer;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.presenter.WebDialogPresenter;
import per.goweii.wanandroid.module.home.view.WebDialogView;
import per.goweii.wanandroid.module.main.adapter.WebDialogPagerAdapter;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebDialog extends DialogLayer implements WebDialogView {

    private final List<Data> urls;
    private final int currPos;

    private WebDialogPresenter presenter = null;
    private OnPageChangedListener mOnPageChangedListener = null;
    private WebDialogPagerAdapter mAdapter;

    public WebDialog(Context context, final List<Data> urls, final int currPos) {
        super(context);
        this.urls = urls;
        this.currPos = currPos;
        contentView(R.layout.dialog_web);
        backgroundColorRes(R.color.dialog_bg);
        cancelableOnTouchOutside(true);
        cancelableOnClickKeyBack(true);
        asStatusBar(R.id.dialog_web_status_bar);
        onClickToDismiss(R.id.dialog_web_iv_close);
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(View target) {
                LinearLayout ll_bottom_bar = target.findViewById(R.id.dialog_web_ll_bottom_bar);
                ViewPager vp = target.findViewById(R.id.dialog_web_vp);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        AnimatorHelper.createZoomAlphaInAnim(vp),
                        AnimatorHelper.createBottomInAnim(ll_bottom_bar)
                );
                return set;
            }

            @Override
            public Animator createOutAnimator(View target) {
                LinearLayout ll_bottom_bar = target.findViewById(R.id.dialog_web_ll_bottom_bar);
                ViewPager vp = target.findViewById(R.id.dialog_web_vp);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        AnimatorHelper.createZoomAlphaOutAnim(vp),
                        AnimatorHelper.createBottomOutAnim(ll_bottom_bar)
                );
                return set;
            }
        });
    }

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        mOnPageChangedListener = onPageChangedListener;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        presenter = new WebDialogPresenter();
        presenter.onCreate(this);
        final ViewPager vp = getView(R.id.dialog_web_vp);
        final ImageView iv_back = getView(R.id.dialog_web_iv_back);
        final CollectView cv_collect = getView(R.id.dialog_web_cv_collect);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (mAdapter != null) {
                    mAdapter.resumeAndPauseOthersAgentWeb(i);
                }
                Data data = urls.get(i);
                if (cv_collect.isChecked() != data.isCollect()) {
                    cv_collect.toggle();
                }
                if (mOnPageChangedListener != null) {
                    mOnPageChangedListener.onPageChanged(i, data);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
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
                Data data = urls.get(vp.getCurrentItem());
                if (!v.isChecked()) {
                    presenter.collect(data, v);
                } else {
                    presenter.uncollect(data, v);
                }
            }
        });
        mAdapter = new WebDialogPagerAdapter(getActivity(), urls);
        mAdapter.setOnDoubleClickListener(new WebDialogPagerAdapter.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(Data data) {
                if (presenter != null) {
                    presenter.collect(data, cv_collect);
                }
            }
        });
        vp.setAdapter(mAdapter);
        vp.setCurrentItem(currPos);
    }

    @Override
    public void onDetach() {
        if (mAdapter != null) {
            mAdapter.destroyAllAgentWeb();
        }
        if (presenter != null) {
            presenter.onDestroy();
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

    public static class Data {
        private int id;
        private String url;
        private boolean collect;

        public Data(int id, String url, boolean collect) {
            this.id = id;
            this.url = url;
            this.collect = collect;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isCollect() {
            return collect;
        }

        public void setCollect(boolean collect) {
            this.collect = collect;
        }
    }

    public interface OnPageChangedListener {
        void onPageChanged(int pos, Data data);
    }

}
