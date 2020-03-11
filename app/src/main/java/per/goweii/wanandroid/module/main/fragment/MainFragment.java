package per.goweii.wanandroid.module.main.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.module.home.fragment.HomeFragment;
import per.goweii.wanandroid.module.mine.fragment.MineFragment;
import per.goweii.wanandroid.module.project.fragment.ProjectFragment;
import per.goweii.wanandroid.module.wxarticle.fragment.WxFragment;
import per.goweii.wanandroid.utils.TM;

public class MainFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.vp_tab)
    ViewPager vp_tab;
    @BindView(R.id.ll_bb)
    LinearLayout ll_bb;
    @BindView(R.id.iv_bb_home)
    ImageView iv_bb_home;
    @BindView(R.id.tv_bb_home)
    TextView tv_bb_home;
    @BindView(R.id.iv_bb_knowledge)
    ImageView iv_bb_knowledge;
    @BindView(R.id.tv_bb_knowledge)
    TextView tv_bb_knowledge;
    @BindView(R.id.iv_bb_wechat)
    ImageView iv_bb_wechat;
    @BindView(R.id.tv_bb_wechat)
    TextView tv_bb_wechat;
    @BindView(R.id.iv_bb_project)
    ImageView iv_bb_project;
    @BindView(R.id.tv_bb_project)
    TextView tv_bb_project;
    @BindView(R.id.iv_bb_mine)
    ImageView iv_bb_mine;
    @BindView(R.id.tv_bb_mine)
    TextView tv_bb_mine;

    private FixedFragmentPagerAdapter mPagerAdapter;
    private long lastClickTime = 0L;
    private int lastClickPos = 0;

    public static MainFragment create() {
        return new MainFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Nullable
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        TM.APP_STARTUP.record("MainFragment initView");
        vp_tab.addOnPageChangeListener(this);
        vp_tab.setOffscreenPageLimit(4);
        mPagerAdapter = new FixedFragmentPagerAdapter(getChildFragmentManager());
        vp_tab.setAdapter(mPagerAdapter);
        mPagerAdapter.setFragmentList(
                HomeFragment.create(),
                KnowledgeNavigationFragment.create(),
                WxFragment.create(),
                ProjectFragment.create(),
                MineFragment.create()
        );
        vp_tab.setCurrentItem(0);
        onPageSelected(vp_tab.getCurrentItem());
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        TM.APP_STARTUP.record("MainFragment onVisible");
    }

    @OnClick({
            R.id.ll_bb_home, R.id.ll_bb_knowledge, R.id.ll_bb_wechat, R.id.ll_bb_project, R.id.ll_bb_mine
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected boolean onClick1(View v) {
        switch (v.getId()) {
            default:
                return false;
            case R.id.ll_bb_home:
                vp_tab.setCurrentItem(0);
                break;
            case R.id.ll_bb_knowledge:
                vp_tab.setCurrentItem(1);
                break;
            case R.id.ll_bb_wechat:
                vp_tab.setCurrentItem(2);
                break;
            case R.id.ll_bb_project:
                vp_tab.setCurrentItem(3);
                break;
            case R.id.ll_bb_mine:
                vp_tab.setCurrentItem(4);
                break;
        }
        notifyScrollTop(vp_tab.getCurrentItem());
        return true;
    }

    private void notifyScrollTop(int pos) {
        long currClickTime = System.currentTimeMillis();
        if (lastClickPos == pos && currClickTime - lastClickTime <= Config.SCROLL_TOP_DOUBLE_CLICK_DELAY) {
            Fragment fragment = mPagerAdapter.getItem(pos);
            if (fragment instanceof ScrollTop) {
                ScrollTop scrollTop = (ScrollTop) fragment;
                scrollTop.scrollTop();
            }
        }
        lastClickPos = pos;
        lastClickTime = currClickTime;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        iv_bb_home.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_knowledge.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_wechat.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_project.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_mine.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        switch (i) {
            default:
                break;
            case 0:
                iv_bb_home.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 1:
                iv_bb_knowledge.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 2:
                iv_bb_wechat.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 3:
                iv_bb_project.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 4:
                iv_bb_mine.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
