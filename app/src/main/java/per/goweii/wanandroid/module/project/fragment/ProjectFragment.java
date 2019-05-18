package per.goweii.wanandroid.module.project.fragment;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarCommon;
import per.goweii.basic.core.adapter.MultiFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.MainActivity;
import per.goweii.wanandroid.module.project.model.ProjectChapterBean;
import per.goweii.wanandroid.module.project.presenter.ProjectPresenter;
import per.goweii.wanandroid.module.project.view.ProjectView;
import per.goweii.wanandroid.utils.MagicIndicatorUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ProjectFragment extends BaseFragment<ProjectPresenter> implements MainActivity.ScrollTop, ProjectView {

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.mi)
    MagicIndicator mi;
    @BindView(R.id.vp)
    ViewPager vp;

    private MultiFragmentPagerAdapter<ProjectChapterBean, ProjectArticleFragment> mAdapter;
    private CommonNavigator mCommonNavigator;

    public static ProjectFragment create() {
        return new ProjectFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project;
    }

    @Nullable
    @Override
    protected ProjectPresenter initPresenter() {
        return new ProjectPresenter();
    }

    @Override
    protected void initView() {
        mAdapter = new MultiFragmentPagerAdapter<>(
                getChildFragmentManager(),
                new MultiFragmentPagerAdapter.FragmentCreator<ProjectChapterBean, ProjectArticleFragment>() {
                    @Override
                    public ProjectArticleFragment create(ProjectChapterBean data) {
                        return ProjectArticleFragment.create(data);
                    }

                    @Override
                    public String getTitle(ProjectChapterBean data) {
                        return data.getName();
                    }
                });
        vp.setAdapter(mAdapter);
        mCommonNavigator = MagicIndicatorUtils.commonNavigator(mi, vp, mAdapter);
    }

    @Override
    protected void loadData() {
        presenter.getProjectChapters();
    }

    @Override
    public void scrollTop() {
    }

    @Override
    public void getProjectChaptersSuccess(int code, List<ProjectChapterBean> data) {
        mAdapter.setDataList(data);
        mCommonNavigator.notifyDataSetChanged();
    }

    @Override
    public void getProjectChaptersFailed(int code, String msg) {
        ToastMaker.showShort(msg);
    }
}
