package per.goweii.wanandroid.module.home.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.adapter.SearchResultAdapter;
import per.goweii.wanandroid.module.home.model.SearchBean;
import per.goweii.wanandroid.module.home.presenter.SearchResultPresenter;
import per.goweii.wanandroid.module.home.view.SearchResultView;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class SearchResultFragment extends BaseFragment<SearchResultPresenter> implements SearchResultView {

    private static final int PAGE_START = 0;

    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private int currPage = PAGE_START;
    private SmartRefreshUtils mSmartRefreshUtils;
    private SearchResultAdapter mAdapter;

    private String mKey;

    public static SearchResultFragment create() {
        return new SearchResultFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Nullable
    @Override
    protected SearchResultPresenter initPresenter() {
        return new SearchResultPresenter();
    }

    @Override
    protected void initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = PAGE_START;
                presenter.search(currPage, mKey);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SearchResultAdapter();
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.search(currPage, mKey);
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
        mAdapter.setOnCollectViewClickListener(new SearchResultAdapter.OnCollectViewClickListener() {
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
    }

    public void search(String key){
        if (!isAdded()) {
            return;
        }
        mAdapter.setNewData(null);
        mKey = key;
        currPage = PAGE_START;
        presenter.search(currPage, key);
    }

    @Override
    public void searchSuccess(int code, SearchBean data) {
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
    public void searchFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
    }
}
