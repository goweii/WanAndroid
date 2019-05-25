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

import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.adapter.CollectionArticleAdapter;
import per.goweii.wanandroid.module.mine.model.CollectionArticleBean;
import per.goweii.wanandroid.module.mine.presenter.CollectionArticlePresenter;
import per.goweii.wanandroid.module.mine.view.CollectionArticleView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionArticleFragment extends BaseFragment<CollectionArticlePresenter> implements ScrollTop, CollectionArticleView {

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private CollectionArticleAdapter mAdapter;

    private int currPage = 0;

    public static CollectionArticleFragment create() {
        return new CollectionArticleFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isCollect()) {
            currPage = 0;
            presenter.getCollectArticleList(currPage, true);
        } else {
            if (event.getArticleId() != -1 || event.getCollectId() != -1) {
                List<CollectionArticleBean.DatasBean> list = mAdapter.getData();
                for (int i = 0; i < list.size(); i++) {
                    CollectionArticleBean.DatasBean item = list.get(i);
                    if (event.getArticleId() != -1) {
                        if (item.getOriginId() == event.getArticleId()) {
                            mAdapter.remove(i);
                            break;
                        }
                    } else if (event.getCollectId() != -1) {
                        if (item.getId() == event.getCollectId()) {
                            mAdapter.remove(i);
                            break;
                        }
                    }
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

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
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
                presenter.getCollectArticleList(currPage, true);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CollectionArticleAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getCollectArticleList(currPage, false);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CollectionArticleBean.DatasBean item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getOriginId(), item.getTitle(), item.getLink());
                }
            }
        });
        mAdapter.setOnCollectViewClickListener(new CollectionArticleAdapter.OnCollectViewClickListener() {
            @Override
            public void onClick(BaseViewHolder helper, CollectView v, int position) {
                CollectionArticleBean.DatasBean item = mAdapter.getItem(position);
                if (item != null) {
                    presenter.uncollect(item, v);
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
        presenter.getCollectArticleList(currPage, false);
    }

    @Override
    public void getCollectArticleListSuccess(int code, CollectionArticleBean data) {
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
        if (currPage == 1) {
            MultiStateUtils.toError(msv);
        }
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            if (rv != null) {
                rv.smoothScrollToPosition(0);
            }
        }
    }
}
