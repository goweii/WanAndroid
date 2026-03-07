package per.goweii.wanandroid.module.main.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.adapter.TabFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.databinding.FragmentMainBinding;
import per.goweii.wanandroid.event.CloseSecondFloorEvent;
import per.goweii.wanandroid.event.MessageCountEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.home.fragment.HomeFragment;
import per.goweii.wanandroid.module.main.adapter.MainTabAdapter;
import per.goweii.wanandroid.module.main.model.TabEntity;
import per.goweii.wanandroid.module.mine.fragment.MineFragment;
import per.goweii.wanandroid.module.question.fragment.QuestionFragment;
import per.goweii.wanandroid.utils.SettingUtils;

public class MainFragment extends BaseFragment<BasePresenter, FragmentMainBinding> {

    @BindView(R.id.vp_tab)
    ViewPager vp_tab;
    @BindView(R.id.ll_bottom_bar)
    LinearLayout ll_bottom_bar;
    //@BindView(R.id.bvef)
    //BackdropVisualEffectView bvef;

    private TabFragmentPagerAdapter.Page<TabEntity> mHomePage;
    private TabFragmentPagerAdapter.Page<TabEntity> mSquarePage;
    private TabFragmentPagerAdapter.Page<TabEntity> mNaviPage;
    private TabFragmentPagerAdapter.Page<TabEntity> mQAPage;
    private TabFragmentPagerAdapter.Page<TabEntity> mMinePage;
    private TabFragmentPagerAdapter<TabEntity> mTabFragmentPagerAdapter;

    public static MainFragment create() {
        return new MainFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCountEvent(MessageCountEvent event) {
        if (isDetached()) return;
        mMinePage.getData().setMsgCount(event.getCount());
        mTabFragmentPagerAdapter.notifyPageDataChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingChangeEvent(SettingChangeEvent event) {
        if (isDetached()) return;
        if (event.isShowQAChanged()) {
            mTabFragmentPagerAdapter.setPages(createPages());
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Nullable
    @Override
    protected FragmentMainBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMainBinding.inflate(inflater, container, false);
    }

    @Nullable
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mTabFragmentPagerAdapter = new TabFragmentPagerAdapter<>(getChildFragmentManager(), vp_tab, ll_bottom_bar, R.layout.tab_item_main);
        mTabFragmentPagerAdapter.setPages(createPages());

        vp_tab.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                new CloseSecondFloorEvent().post();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //bvef.setShowDebugInfo(false);
        //bvef.setOverlayColor(ResUtils.getThemeColor(bvef, R.attr.colorBottomBarOverlay));
        //bvef.setSimpleSize(8);
        //bvef.setVisualEffect(new RSBlurEffect(getContext(), 8));

        ViewCompat.setOnApplyWindowInsetsListener(binding.flBottomBar, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                final int b = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                binding.flBottomBar.setPadding(0, 0, 0, b);
                return insets;
            }
        });
    }

    @NonNull
    private List<TabFragmentPagerAdapter.Page<TabEntity>> createPages() {
        final List<TabFragmentPagerAdapter.Page<TabEntity>> pages = new ArrayList<>();

        if (mHomePage == null) {
            mHomePage = new TabFragmentPagerAdapter.Page<>(R.string.homepage, HomeFragment.create(),
                    new TabEntity(getString(R.string.homepage), R.drawable.ic_bottom_bar_home, -1),
                    new MainTabAdapter());
        }
        pages.add(mHomePage);

        if (mSquarePage == null) {
            mSquarePage = new TabFragmentPagerAdapter.Page<>(2, UserArticleFragment.create(),
                    new TabEntity(getString(R.string.square), R.drawable.ic_bottom_bar_square, -1),
                    new MainTabAdapter());
        }
        pages.add(mSquarePage);

        if (SettingUtils.getInstance().isShowQA()) {
            if (mQAPage == null) {
                mQAPage = new TabFragmentPagerAdapter.Page<>(3, QuestionFragment.create(),
                        new TabEntity(getString(R.string.qa), R.drawable.ic_bottom_bar_ques, -1),
                        new MainTabAdapter());
            }
            pages.add(mQAPage);
        } else {
            mQAPage = null;
        }

        if (mNaviPage == null) {
            mNaviPage = new TabFragmentPagerAdapter.Page<>(4, KnowledgeNavigationFragment.create(),
                    new TabEntity(getString(R.string.navigation), R.drawable.ic_bottom_bar_navi, -1),
                    new MainTabAdapter());
        }
        pages.add(mNaviPage);

        if (mMinePage == null) {
            mMinePage = new TabFragmentPagerAdapter.Page<>(5, MineFragment.create(),
                    new TabEntity(getString(R.string.mine), R.drawable.ic_bottom_bar_mine, -1),
                    new MainTabAdapter());
        }
        pages.add(mMinePage);

        return pages;
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
    }
}
