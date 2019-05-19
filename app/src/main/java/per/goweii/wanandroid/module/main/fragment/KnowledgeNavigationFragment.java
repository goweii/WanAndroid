package per.goweii.wanandroid.module.main.fragment;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarEx;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.knowledge.fragment.KnowledgeFragment;
import per.goweii.wanandroid.module.navigation.fragment.NaviFragment;
import per.goweii.wanandroid.utils.MagicIndicatorUtils;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgeNavigationFragment extends BaseFragment {

    @BindView(R.id.ab)
    ActionBarEx ab;
    @BindView(R.id.vp)
    ViewPager vp;

    public static KnowledgeNavigationFragment create() {
        return new KnowledgeNavigationFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_knowledge_navigation;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        FixedFragmentPagerAdapter adapter = new FixedFragmentPagerAdapter(getChildFragmentManager());
        adapter.setTitles("体系", "导航");
        adapter.setFragmentList(
                KnowledgeFragment.create(),
                NaviFragment.create()
        );
        vp.setAdapter(adapter);
        MagicIndicatorUtils.commonNavigator(ab.getView(R.id.mi), vp, adapter);
    }

    @Override
    protected void loadData() {

    }
}
