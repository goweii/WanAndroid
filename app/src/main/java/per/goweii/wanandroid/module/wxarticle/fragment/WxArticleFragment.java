package per.goweii.wanandroid.module.wxarticle.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.ScrollTopEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.ChapterBean;
import per.goweii.wanandroid.module.wxarticle.presenter.WxArticlePresenter;
import per.goweii.wanandroid.module.wxarticle.view.WxArticleView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class WxArticleFragment extends BaseFragment<WxArticlePresenter> implements WxArticleView {

    private static final int PAGE_START = 1;

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private ArticleAdapter mAdapter;

    private ChapterBean mChapterBean;
    private int mPosition = -1;

    private int currPage = PAGE_START;

    public static WxArticleFragment create(ChapterBean chapterBean, int position) {
        WxArticleFragment fragment = new WxArticleFragment();
        Bundle args = new Bundle(2);
        args.putSerializable("chapterBean", chapterBean);
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
        mAdapter.notifyCollectionEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isLogin()) {
            currPage = PAGE_START;
            getWxArticleList(true);
        } else {
            mAdapter.notifyAllUnCollect();
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
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_wx_article;
    }

    @Nullable
    @Override
    protected WxArticlePresenter initPresenter() {
        return new WxArticlePresenter();
    }

    @Override
    protected void initView() {
        Bundle args = getArguments();
        if (args != null) {
            mChapterBean = (ChapterBean) args.getSerializable("chapterBean");
            mPosition = args.getInt("position", -1);
        }

        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = PAGE_START;
                getWxArticleList(true);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArticleAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getWxArticleList(false);
            }
        }, rv);
        mAdapter.setOnItemChildViewClickListener(new ArticleAdapter.OnItemChildViewClickListener() {
            @Override
            public void onCollectClick(BaseViewHolder helper, CollectView v, int position) {
                ArticleBean item = mAdapter.getArticleBean(position);
                if (item != null) {
                    if (v.isChecked()) {
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
                MultiStateUtils.toLoading(msv);
                currPage = PAGE_START;
                getWxArticleList(false);
            }
        });
    }

    @Override
    protected void loadData() {
        if (mChapterBean != null) {
            MultiStateUtils.toLoading(msv);
            presenter.getWxArticleListCache(mChapterBean.getId(), currPage);
        } else {
            MultiStateUtils.toError(msv);
        }
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        if (isFirstVisible) {
            currPage = PAGE_START;
            getWxArticleList(false);
        }
    }

    public void getWxArticleList(boolean refresh) {
        if (mChapterBean != null) {
            presenter.getWxArticleList(mChapterBean.getId(), currPage, true);
        }
    }

    @Override
    public void getWxArticleListSuccess(int code, ArticleListBean data) {
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
    public void getWxArticleListFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
        if (currPage == PAGE_START) {
            MultiStateUtils.toError(msv);
        }
    }

    @Override
    public void getWxArticleListSearchSuccess(int code, ArticleListBean data) {
    }

    @Override
    public void getWxArticleListSearchFailed(int code, String msg) {
    }
}
