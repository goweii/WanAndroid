package per.goweii.wanandroid.module.project.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.ScrollTopEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.project.adapter.ProjectArticleAdapter;
import per.goweii.wanandroid.module.project.model.ProjectArticleBean;
import per.goweii.wanandroid.module.project.model.ProjectChapterBean;
import per.goweii.wanandroid.module.project.presenter.ProjectArticlePresenter;
import per.goweii.wanandroid.module.project.view.ProjectArticleView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ProjectArticleFragment extends BaseFragment<ProjectArticlePresenter> implements ProjectArticleView {

    private static final int PAGE_START = 1;

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private ProjectArticleAdapter mAdapter;

    private ProjectChapterBean mProjectChapterBean;
    private int mPosition = -1;

    private int currPage = PAGE_START;

    public static ProjectArticleFragment create(ProjectChapterBean projectChapterBean, int position) {
        ProjectArticleFragment fragment = new ProjectArticleFragment();
        Bundle args = new Bundle(2);
        args.putSerializable("projectChapterBean", projectChapterBean);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.getArticleId() == -1) {
            return;
        }
        List<ProjectArticleBean.DatasBean> list = mAdapter.getData();
        for (int i = 0; i < list.size(); i++) {
            ProjectArticleBean.DatasBean item = list.get(i);
            if (item.getId() == event.getArticleId()) {
                if (item.isCollect() != event.isCollect()) {
                    item.setCollect(event.isCollect());
                    mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
                }
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isLogin()) {
            currPage = PAGE_START;
            getProjectArticleList(true);
        } else {
            List<ProjectArticleBean.DatasBean> list = mAdapter.getData();
            for (int i = 0; i < list.size(); i++) {
                ProjectArticleBean.DatasBean item = list.get(i);
                if (item.isCollect()) {
                    item.setCollect(false);
                    mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingChangeEvent(SettingChangeEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isRvAnimChanged()) {
            RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollTopEvent(ScrollTopEvent event) {
        if (!getClass().equals(event.getClazz())) {
            return;
        }
        if (mPosition != event.getPosition()) {
            return;
        }
        if (isAdded() && !isDetached()) {
            if (rv != null) {
                rv.smoothScrollToPosition(0);
            }
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project_article;
    }

    @Nullable
    @Override
    protected ProjectArticlePresenter initPresenter() {
        return new ProjectArticlePresenter();
    }

    @Override
    protected void initView() {
        Bundle args = getArguments();
        if (args != null) {
            mProjectChapterBean = (ProjectChapterBean) args.getSerializable("projectChapterBean");
            mPosition = args.getInt("position", -1);
        }

        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = PAGE_START;
                getProjectArticleList(true);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ProjectArticleAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getProjectArticleList(false);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ProjectArticleBean.DatasBean item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getId(), item.getTitle(), item.getLink());
                }
            }
        });
        mAdapter.setOnCollectViewClickListener(new ProjectArticleAdapter.OnCollectViewClickListener() {
            @Override
            public void onClick(BaseViewHolder helper, CollectView v, int position) {
                ProjectArticleBean.DatasBean item = mAdapter.getItem(position);
                if (item != null) {
                    if (!v.isChecked()) {
                        presenter.collect(item, v);
                    } else {
                        presenter.uncollect(item, v);
                    }
                }
            }
        });
        rv.setAdapter(mAdapter);
        MultiStateUtils.setEmptyAndErrorClick(msv, new SimpleListener() {
            @Override
            public void onResult() {
                loadData();
            }
        });
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        getProjectArticleList(false);
    }

    public void getProjectArticleList(boolean refresh) {
        if (mProjectChapterBean != null) {
            presenter.getProjectArticleList(mProjectChapterBean.getId(), currPage, refresh);
        }
    }

    @Override
    public void getProjectArticleListSuccess(int code, ProjectArticleBean data) {
        currPage = data.getCurPage();
        if (currPage == 1) {
            mAdapter.setNewData(data.getDatas());
            mAdapter.setEnableLoadMore(true);
            if (data.getDatas() == null || data.getDatas().isEmpty()) {
                MultiStateUtils.toEmpty(msv);
            } else {
                MultiStateUtils.toContent(msv);
            }
        } else {
            mAdapter.addData(data.getDatas());
            mAdapter.loadMoreComplete();
        }
        currPage++;
        if (data.isOver()) {
            mAdapter.loadMoreEnd();
        }
        mSmartRefreshUtils.success();
    }

    @Override
    public void getProjectArticleListFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
        if (currPage == PAGE_START) {
            MultiStateUtils.toError(msv);
        }
    }
}
