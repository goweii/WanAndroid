package per.goweii.wanandroid.module.navigation.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kennyc.view.MultiStateView;

import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.navigation.adapter.NaviAdapter;
import per.goweii.wanandroid.module.navigation.model.NaviBean;
import per.goweii.wanandroid.module.navigation.presenter.NaviPresenter;
import per.goweii.wanandroid.module.navigation.view.NaviView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class NaviFragment extends BaseFragment<NaviPresenter> implements ScrollTop, NaviView {

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.rv)
    RecyclerView rv;

    private NaviAdapter mAdapter;

    public static NaviFragment create() {
        return new NaviFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_knowledge_navigation_child;
    }

    @Nullable
    @Override
    protected NaviPresenter initPresenter() {
        return new NaviPresenter();
    }

    @Override
    protected void initView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new NaviAdapter();
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnItemClickListener(new NaviAdapter.OnItemClickListener() {
            @Override
            public void onClick(ArticleBean bean, int pos) {
                WebActivity.start(getContext(), bean);
            }
        });
        rv.setAdapter(mAdapter);
        MultiStateUtils.setEmptyAndErrorClick(msv, new SimpleListener() {
            @Override
            public void onResult() {
                MultiStateUtils.toLoading(msv);
                presenter.getKnowledgeList();
            }
        });
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        presenter.getKnowledgeListCache();
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        if (isFirstVisible) {
            presenter.getKnowledgeList();
        }
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }

    @Override
    public void getNaviListSuccess(int code, List<NaviBean> data) {
        mAdapter.setNewData(data);
        if (data == null || data.isEmpty()) {
            MultiStateUtils.toEmpty(msv);
        } else {
            MultiStateUtils.toContent(msv);
        }
    }

    @Override
    public void getNaviListFail(int code, String msg) {
        ToastMaker.showShort(msg);
        MultiStateUtils.toError(msv);
    }
}
