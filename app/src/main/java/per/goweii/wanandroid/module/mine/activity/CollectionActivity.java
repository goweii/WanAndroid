package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.adapter.CollectionAdapter;
import per.goweii.wanandroid.module.mine.model.CollectionBean;
import per.goweii.wanandroid.module.mine.presenter.CollectionPresenter;
import per.goweii.wanandroid.module.mine.view.CollectionView;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionActivity extends BaseActivity<CollectionPresenter> implements CollectionView {

    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private CollectionAdapter mAdapter;

    private int currPage = 0;

    public static void start(Context context) {
        Intent intent = new Intent(context, CollectionActivity.class);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDestroyed()) {
            return;
        }
        if (event.isCollect()) {
            currPage = 0;
            presenter.getCollectList(currPage);
        } else {
            List<CollectionBean.DatasBean> list = mAdapter.getData();
            for (int i = 0; i < list.size(); i++) {
                CollectionBean.DatasBean item = list.get(i);
                if (item.getOriginId() == event.getId()) {
                    mAdapter.remove(i);
                    break;
                }
            }
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Nullable
    @Override
    protected CollectionPresenter initPresenter() {
        return new CollectionPresenter();
    }

    @Override
    protected void initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getCollectList(currPage);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CollectionAdapter();
        mAdapter.openLoadAnimation();
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getCollectList(currPage);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CollectionBean.DatasBean item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getTitle(), item.getLink());
                }
            }
        });
        mAdapter.setOnCollectViewClickListener(new CollectionAdapter.OnCollectViewClickListener() {
            @Override
            public void onClick(BaseViewHolder helper, CollectView v, int position) {
                CollectionBean.DatasBean item = mAdapter.getItem(position);
                if (item != null) {
                    presenter.uncollect(item, v);
                }
            }
        });
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        presenter.getCollectList(currPage);
    }

    @Override
    public void getCollectListSuccess(int code, CollectionBean data) {
        currPage = data.getCurPage();
        if (currPage == 1) {
            mAdapter.setNewData(data.getDatas());
            mAdapter.setEnableLoadMore(true);
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
    public void getCollectFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
    }
}
