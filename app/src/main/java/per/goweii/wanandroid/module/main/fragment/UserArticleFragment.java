package per.goweii.wanandroid.module.main.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.gms.ads.nativead.NativeAd;
import com.kennyc.view.MultiStateView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.utils.GoogleAdUnitIds;
import per.goweii.component.ad.NativeAdProvider;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.databinding.FragmentUserArticleBinding;
import per.goweii.wanandroid.event.ArticleShareEvent;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.module.main.activity.ShareArticleActivity;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.NativeAdBean;
import per.goweii.wanandroid.module.main.presenter.UserArticlePresenter;
import per.goweii.wanandroid.module.main.view.UserArticleView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.utils.cdkey.CDKeyUtils;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class UserArticleFragment extends BaseFragment<UserArticlePresenter, FragmentUserArticleBinding> implements UserArticleView, RvScrollTopUtils.ScrollTop {

    private static final int PAGE_START = 0;

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private ArticleAdapter mAdapter;

    private int currPage = PAGE_START;

    private long lastClickTime = 0L;

    public static UserArticleFragment create() {
        return new UserArticleFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (event.getArticleId() == -1) {
            return;
        }
        mAdapter.notifyCollectionEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (event.isLogin()) {
            currPage = PAGE_START;
            getProjectArticleList(true);
        } else {
            mAdapter.notifyAllUnCollect();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticleShareEvent(ArticleShareEvent event) {
        currPage = PAGE_START;
        getProjectArticleList(true);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Nullable
    @Override
    protected FragmentUserArticleBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentUserArticleBinding.inflate(inflater, container, false);
    }

    @Nullable
    @Override
    protected UserArticlePresenter initPresenter() {
        return new UserArticlePresenter();
    }

    @Override
    protected void initView() {
        abc.getTitleTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currClickTime = System.currentTimeMillis();
                if (currClickTime - lastClickTime <= Config.SCROLL_TOP_DOUBLE_CLICK_DELAY) {
                    RvScrollTopUtils.smoothScrollTop(rv);
                }
                lastClickTime = currClickTime;
            }
        });
        abc.setOnRightIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                ShareArticleActivity.start(getContext());
            }
        });
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
        mAdapter = new ArticleAdapter();
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getProjectArticleList(true);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBean item = mAdapter.getArticleItem(position);
                if (item != null) {
                    UrlOpenUtils.Companion.with(item).open(getContext());
                }
            }
        });
        mAdapter.setOnItemChildViewClickListener(new ArticleAdapter.OnItemChildViewClickListener() {
            @Override
            public void onCollectClick(BaseViewHolder helper, CollectView v, int position) {
                ArticleBean item = mAdapter.getArticleItem(position);
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
                getProjectArticleList(true);
            }
        });
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        currPage = PAGE_START;
        presenter.getUserArticleListCache(currPage);

//        if (!CDKeyUtils.getInstance().isActive()) {
//            new NativeAdProvider(requireActivity(), getViewLifecycleOwner(), GoogleAdUnitIds.ARTICLE_LIST_NATIVE_AD).load(new SimpleCallback<NativeAd>() {
//                @Override
//                public void onResult(NativeAd data) {
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
//                    if (layoutManager != null) {
//                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//                        mAdapter.addData(lastVisibleItemPosition + 1, new NativeAdBean(data));
//                    }
//                }
//            });
//        }
    }

    @Override
    protected void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        if (isFirstVisible) {
            currPage = PAGE_START;
            getProjectArticleList(false);
        }
    }

    public void getProjectArticleList(boolean refresh) {
        presenter.getUserArticleList(currPage, refresh);
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }

    @Override
    public void getUserArticleListSuccess(int code, ArticleListBean data) {
        currPage = data.getCurPage() + PAGE_START;
        if (data.getCurPage() == 1) {
            Map<Integer, NativeAdBean> ads = new HashMap<>(1);
            List<MultiItemEntity> oldList = mAdapter.getData();
            for (int i = 0; i < oldList.size(); i++) {
                MultiItemEntity item = oldList.get(i);
                if (item.getItemType() == ArticleAdapter.ITEM_TYPE_AD) {
                    ads.put(i, (NativeAdBean) item);
                }
            }
            List<MultiItemEntity> newList = new ArrayList<>(data.getDatas());
            if (!ads.isEmpty()) {
                for (Map.Entry<Integer, NativeAdBean> entry : ads.entrySet()) {
                    int i = entry.getKey();
                    if (i > 0 && i < newList.size()) {
                        newList.add(i, entry.getValue());
                    } else {
                        newList.add(entry.getValue());
                    }
                }
            }
            mAdapter.setNewData(newList);
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
    public void getUserArticleListFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
        if (currPage == PAGE_START) {
            MultiStateUtils.toError(msv);
        }
    }
}
