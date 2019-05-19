package per.goweii.wanandroid.module.wxarticle.fragment;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarEx;
import per.goweii.basic.core.adapter.MultiFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.MainActivity;
import per.goweii.wanandroid.module.wxarticle.model.WxChapterBean;
import per.goweii.wanandroid.module.wxarticle.presenter.WxPresenter;
import per.goweii.wanandroid.module.wxarticle.view.WxView;
import per.goweii.wanandroid.utils.MagicIndicatorUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WxFragment extends BaseFragment<WxPresenter> implements MainActivity.ScrollTop, WxView {

    @BindView(R.id.ab)
    ActionBarEx ab;
    @BindView(R.id.vp)
    ViewPager vp;

    private MultiFragmentPagerAdapter<WxChapterBean, WxArticleFragment> mAdapter;
    private CommonNavigator mCommonNavigator;

    public static WxFragment create() {
        return new WxFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wx;
    }

    @Nullable
    @Override
    protected WxPresenter initPresenter() {
        return new WxPresenter();
    }

    @Override
    protected void initView() {
        mAdapter = new MultiFragmentPagerAdapter<>(
                getChildFragmentManager(),
                new MultiFragmentPagerAdapter.FragmentCreator<WxChapterBean, WxArticleFragment>() {
                    @Override
                    public WxArticleFragment create(WxChapterBean data) {
                        return WxArticleFragment.create(data);
                    }

                    @Override
                    public String getTitle(WxChapterBean data) {
                        return data.getName();
                    }
                });
        vp.setAdapter(mAdapter);
        mCommonNavigator = MagicIndicatorUtils.commonNavigator((MagicIndicator) ab.getTitleBarChild(), vp, mAdapter);
    }

    @Override
    protected void loadData() {
        presenter.getWxArticleChapters();
    }

    @Override
    public void scrollTop() {
    }

    @Override
    public void getWxArticleChaptersSuccess(int code, List<WxChapterBean> data) {
        mAdapter.setDataList(data);
        mCommonNavigator.notifyDataSetChanged();
    }

    @Override
    public void getWxArticleChaptersFailed(int code, String msg) {
        ToastMaker.showShort(msg);
    }
}
