package per.goweii.wanandroid.module.main.fragment;

import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import per.goweii.basic.core.adapter.TabFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.fragment.HomeFragment;
import per.goweii.wanandroid.module.main.adapter.MainTabAdapter;
import per.goweii.wanandroid.module.main.model.TabEntity;
import per.goweii.wanandroid.module.mine.fragment.MineFragment;
import per.goweii.wanandroid.module.question.fragment.QuestionFragment;

public class MainFragment extends BaseFragment {

    @BindView(R.id.vp_tab)
    ViewPager vp_tab;
    @BindView(R.id.ll_bb)
    LinearLayout ll_bb;
    private TabFragmentPagerAdapter.Page<TabEntity> mMinePage;
    private TabFragmentPagerAdapter<TabEntity> mTabFragmentPagerAdapter;

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
        mTabFragmentPagerAdapter = new TabFragmentPagerAdapter<>(getChildFragmentManager(), vp_tab, ll_bb, R.layout.tab_item_main);
        mMinePage = new TabFragmentPagerAdapter.Page<>(MineFragment.create(), new TabEntity("我的", R.drawable.ic_bottom_bar_mine, -1), new MainTabAdapter());
        mTabFragmentPagerAdapter.setPages(
                new TabFragmentPagerAdapter.Page<>(HomeFragment.create(), new TabEntity("首页", R.drawable.ic_bottom_bar_home, -1), new MainTabAdapter()),
                new TabFragmentPagerAdapter.Page<>(QuestionFragment.create(), new TabEntity("问答", R.drawable.ic_bottom_bar_wechat, -1), new MainTabAdapter()),
                new TabFragmentPagerAdapter.Page<>(KnowledgeNavigationFragment.create(), new TabEntity("体系", R.drawable.ic_bottom_bar_navi, -1), new MainTabAdapter()),
                mMinePage
        );
    }

    @Override
    protected void loadData() {
        mMinePage.getData().setMsgCount(0);
        mTabFragmentPagerAdapter.notifyPageDataChanged();
    }

    @Override
    protected void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
    }
}
