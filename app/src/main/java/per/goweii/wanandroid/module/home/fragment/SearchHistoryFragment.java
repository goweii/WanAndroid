package per.goweii.wanandroid.module.home.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.ui.dialog.TipDialog;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.activity.SearchActivity;
import per.goweii.wanandroid.module.home.model.HotKeyBean;
import per.goweii.wanandroid.module.home.presenter.SearchHistoryPresenter;
import per.goweii.wanandroid.module.home.view.SearchHistoryView;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class SearchHistoryFragment extends BaseFragment<SearchHistoryPresenter> implements SearchHistoryView {

    @BindView(R.id.rv_hot)
    RecyclerView rv_hot;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;

    private BaseQuickAdapter<HotKeyBean, BaseViewHolder> mHotAdapter;
    private BaseQuickAdapter<String, BaseViewHolder> mHistoryAdapter;

    public static SearchHistoryFragment create() {
        return new SearchHistoryFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_history;
    }

    @Nullable
    @Override
    protected SearchHistoryPresenter initPresenter() {
        return new SearchHistoryPresenter();
    }

    @Override
    protected void initView() {
        rv_hot.setNestedScrollingEnabled(false);
        rv_hot.setHasFixedSize(true);
        rv_hot.setLayoutManager(new FlexboxLayoutManager(getContext()));
        mHotAdapter = new BaseQuickAdapter<HotKeyBean, BaseViewHolder>(R.layout.rv_item_search_hot) {
            @Override
            protected void convert(BaseViewHolder helper, HotKeyBean item) {
                helper.setText(R.id.tv_key, item.getName());
            }
        };
        mHotAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HotKeyBean item = mHotAdapter.getItem(position);
                if (item != null) {
                    search(item.getName());
                }
            }
        });
        rv_hot.setAdapter(mHotAdapter);
        rv_history.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.rv_item_search_history) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_key, item)
                        .addOnClickListener(R.id.iv_remove);
            }
        };
        mHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String key = mHistoryAdapter.getItem(position);
                search(key);
            }
        });
        mHistoryAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mHistoryAdapter.remove(position);
                presenter.saveHistory(mHistoryAdapter.getData());
            }
        });
        rv_history.setAdapter(mHistoryAdapter);
    }

    @Override
    protected void loadData() {
        presenter.getHotKeyList();
        mHistoryAdapter.setNewData(presenter.getHistory());
    }

    private void search(String key) {
        if (getActivity() instanceof SearchActivity) {
            SearchActivity activity = (SearchActivity) getActivity();
            activity.search(key);
        }
    }

    @OnClick({R.id.tv_clean})
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onClick2(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_clean:
                TipDialog.with(getContext())
                        .message("确定要清除搜索历史？")
                        .onYes(new SimpleCallback<Void>() {
                            @Override
                            public void onResult(Void data) {
                                mHistoryAdapter.setNewData(null);
                                presenter.saveHistory(null);
                            }
                        })
                        .show();
                break;
        }
    }

    public void addHistory(String key) {
        mHistoryAdapter.addData(0, key);
        int max = SettingUtils.getInstance().getSearchHistoryMaxCount();
        List<String> list = mHistoryAdapter.getData();
        if (list.size() > max) {
            mHistoryAdapter.remove(list.size() - 1);
        }
        rv_history.smoothScrollToPosition(0);
        presenter.saveHistory(mHistoryAdapter.getData());
    }

    @Override
    public void getHotKeyListSuccess(int code, List<HotKeyBean> data) {
        mHotAdapter.setNewData(data);
    }

    @Override
    public void getHotKeyListFail(int code, String msg) {
    }
}
