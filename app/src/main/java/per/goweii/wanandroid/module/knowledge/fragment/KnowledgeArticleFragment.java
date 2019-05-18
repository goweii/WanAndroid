package per.goweii.wanandroid.module.knowledge.fragment;

import android.os.Bundle;
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
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.module.knowledge.adapter.KnowledgeArticleAdapter;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeArticleBean;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeBean;
import per.goweii.wanandroid.module.knowledge.presenter.KnowledgeArticlePresenter;
import per.goweii.wanandroid.module.knowledge.view.KnowledgeArticleView;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgeArticleFragment extends BaseFragment<KnowledgeArticlePresenter> implements KnowledgeArticleView {

    private static final int PAGE_START = 0;

    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private KnowledgeArticleAdapter mAdapter;

    private KnowledgeBean mKnowledgeBean;

    private int currPage = PAGE_START;

    public static KnowledgeArticleFragment create(KnowledgeBean knowledgeBean) {
        KnowledgeArticleFragment fragment = new KnowledgeArticleFragment();
        Bundle args = new Bundle(1);
        args.putSerializable("knowledgeBean", knowledgeBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDetached()) {
            return;
        }
        List<ArticleBean> list = mAdapter.getData();
        for (int i = 0; i < list.size(); i++) {
            ArticleBean item = list.get(i);
            if (item.getId() == event.getId()) {
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
            getKnowledgeArticleList();
        } else {
            List<ArticleBean> list = mAdapter.getData();
            for (int i = 0; i < list.size(); i++) {
                ArticleBean item = list.get(i);
                if (item.isCollect()) {
                    item.setCollect(false);
                    mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
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
        return R.layout.fragment_knowledge_article;
    }

    @Nullable
    @Override
    protected KnowledgeArticlePresenter initPresenter() {
        return new KnowledgeArticlePresenter();
    }

    @Override
    protected void initView() {
        Bundle args = getArguments();
        if (args != null) {
            mKnowledgeBean = (KnowledgeBean) args.getSerializable("knowledgeBean");
        }

        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = PAGE_START;
                getKnowledgeArticleList();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new KnowledgeArticleAdapter();
        mAdapter.openLoadAnimation();
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getKnowledgeArticleList();
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBean item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getTitle(), item.getLink());
                }
            }
        });
        mAdapter.setOnCollectViewClickListener(new KnowledgeArticleAdapter.OnCollectViewClickListener() {
            @Override
            public void onClick(BaseViewHolder helper, CollectView v, int position) {
                ArticleBean item = mAdapter.getItem(position);
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
    }

    @Override
    protected void loadData() {
        getKnowledgeArticleList();
    }

    public void getKnowledgeArticleList() {
        if (mKnowledgeBean != null) {
            presenter.getKnowledgeArticleList(mKnowledgeBean.getId(), currPage);
        }
    }

    @Override
    public void getKnowledgeArticleListSuccess(int code, KnowledgeArticleBean data) {
        if (currPage == PAGE_START) {
            mAdapter.setNewData(data.getDatas());
            mAdapter.setEnableLoadMore(true);
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
    public void getKnowledgeArticleListFail(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
    }
}
