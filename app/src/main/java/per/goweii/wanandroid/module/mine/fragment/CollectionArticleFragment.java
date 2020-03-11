package per.goweii.wanandroid.module.mine.fragment;

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

import butterknife.BindView;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.mine.presenter.CollectionArticlePresenter;
import per.goweii.wanandroid.module.mine.view.CollectionArticleView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class CollectionArticleFragment extends BaseFragment<CollectionArticlePresenter> implements ScrollTop, CollectionArticleView {

    public static final int PAGE_START = 0;

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private ArticleAdapter mAdapter;

    private int currPage = PAGE_START;

    public static CollectionArticleFragment create() {
        return new CollectionArticleFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isCollect()) {
            currPage = PAGE_START;
            presenter.getCollectArticleList(currPage, true);
        } else {
            if (event.getArticleId() != -1 || event.getCollectId() != -1) {
                mAdapter.forEach(new ArticleAdapter.ArticleForEach() {
                    @Override
                    public boolean forEach(int dataPos, int adapterPos, ArticleBean bean) {
                        if (event.getArticleId() != -1) {
                            if (bean.getOriginId() == event.getArticleId()) {
                                mAdapter.remove(adapterPos);
                                return true;
                            }
                        } else if (event.getCollectId() != -1) {
                            if (bean.getId() == event.getCollectId()) {
                                mAdapter.remove(adapterPos);
                                return true;
                            }
                        }
                        return false;
                    }
                });
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

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_collection_article;
    }

    @Nullable
    @Override
    protected CollectionArticlePresenter initPresenter() {
        return new CollectionArticlePresenter();
    }

    @Override
    protected void initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = PAGE_START;
                presenter.getCollectArticleList(currPage, true);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArticleAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getCollectArticleList(currPage, true);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBean item = mAdapter.getArticleBean(position);
                if (item != null) {
                    WebActivity.start(getContext(), item);
                }
            }
        });
        mAdapter.setOnItemChildViewClickListener(new ArticleAdapter.OnItemChildViewClickListener() {
            @Override
            public void onCollectClick(BaseViewHolder helper, CollectView v, int position) {
                ArticleBean item = mAdapter.getArticleBean(position);
                if (item != null) {
                    presenter.uncollect(item, v);
                }
            }
        });
        rv.setAdapter(mAdapter);
        MultiStateUtils.setEmptyAndErrorClick(msv, new SimpleListener() {
            @Override
            public void onResult() {
                MultiStateUtils.toLoading(msv);
                currPage = PAGE_START;
                presenter.getCollectArticleList(currPage, true);
            }
        });
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        presenter.getCollectArticleListCache(currPage);
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        if (isFirstVisible) {
            currPage = PAGE_START;
            presenter.getCollectArticleList(currPage, true);
        }
    }

    @Override
    public void getCollectArticleListSuccess(int code, ArticleListBean data) {
        if (data.getDatas() != null) {
            for (ArticleBean articleBean : data.getArticles()) {
                articleBean.setCollect(true);
            }
        }
        currPage = data.getCurPage() + PAGE_START;
        if (data.getCurPage() == 1) {
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
        if (data.isOver()) {
            mAdapter.loadMoreEnd();
        }
        mSmartRefreshUtils.success();
    }

    @Override
    public void getCollectArticleListFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
        if (currPage == PAGE_START) {
            MultiStateUtils.toError(msv);
        }
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }
}
