package per.goweii.wanandroid.module.home.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.ad.widget.AdContainer;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * GitHub: https://github.com/goweii
 */
public class SearchHistoryFragment extends BaseFragment<SearchHistoryPresenter> implements SearchHistoryView {

    @BindView(R.id.rv_hot)
    RecyclerView rv_hot;
    @BindView(R.id.ll_history)
    LinearLayout ll_history;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;
    @BindView(R.id.tv_clean)
    TextView tv_clean;
    @BindView(R.id.tv_down)
    TextView tv_down;
    @BindView(R.id.adc)
    AdContainer adc;

    private BaseQuickAdapter<HotKeyBean, BaseViewHolder> mHotAdapter;
    private BaseQuickAdapter<String, BaseViewHolder> mHistoryAdapter;

    private boolean mRemoveMode = false;
    private boolean mRemoveModeChanging = false;

    public static SearchHistoryFragment create() {
        return new SearchHistoryFragment();
    }

    @Override
    protected int getLayoutRes() {
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
        rv_history.setLayoutManager(new FlexboxLayoutManager(getContext()));
        mHistoryAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.rv_item_search_history) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_key, item);
                helper.addOnClickListener(R.id.iv_remove);
                ImageView iv_remove = helper.getView(R.id.iv_remove);
                if (!mRemoveModeChanging) {
                    helper.setVisible(R.id.iv_remove, mRemoveMode);
                } else {
                    if (mRemoveMode) {
                        ScaleAnimation scaleAnimation = new ScaleAnimation(
                                0F, 1F, 0F, 1F,
                                Animation.RELATIVE_TO_SELF, 0.5F,
                                Animation.RELATIVE_TO_SELF, 0.5F
                        );
                        scaleAnimation.setDuration(300);
                        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                iv_remove.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mRemoveModeChanging = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        iv_remove.startAnimation(scaleAnimation);
                    } else {
                        ScaleAnimation scaleAnimation = new ScaleAnimation(
                                1F, 0F, 1F, 0F,
                                Animation.RELATIVE_TO_SELF, 0.5F,
                                Animation.RELATIVE_TO_SELF, 0.5F
                        );
                        scaleAnimation.setDuration(300);
                        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mRemoveModeChanging = false;
                                iv_remove.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        iv_remove.startAnimation(scaleAnimation);
                    }
                }
            }
        };
        mHistoryAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                changeRemoveMode(!mRemoveMode);
                return true;
            }
        });
        mHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mRemoveMode) {
                    String key = mHistoryAdapter.getItem(position);
                    search(key);
                }
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
        changeHistoryVisible();
    }

    private void changeHistoryVisible() {
        if (mHistoryAdapter == null) {
            ll_history.setVisibility(View.GONE);
        } else {
            if (mHistoryAdapter.getData().isEmpty()) {
                ll_history.setVisibility(View.GONE);
            } else {
                ll_history.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeRemoveMode(boolean removeMode) {
        if (mRemoveMode == removeMode) {
            return;
        }
        mRemoveModeChanging = true;
        mRemoveMode = removeMode;
        mHistoryAdapter.notifyDataSetChanged();
        if (removeMode) {
            tv_down.setVisibility(View.VISIBLE);
            tv_clean.setVisibility(View.GONE);
        } else {
            tv_down.setVisibility(View.GONE);
            tv_clean.setVisibility(View.VISIBLE);
        }
    }

    private void search(String key) {
        if (getActivity() instanceof SearchActivity) {
            SearchActivity activity = (SearchActivity) getActivity();
            activity.search(key);
        }
    }

    @OnClick({R.id.tv_clean, R.id.tv_down})
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
                                changeHistoryVisible();
                                presenter.saveHistory(null);
                            }
                        })
                        .show();
                break;
            case R.id.tv_down:
                changeRemoveMode(false);
                break;
        }
    }

    public void addHistory(String key) {
        List<String> datas = mHistoryAdapter.getData();
        int index = datas.indexOf(key);
        if (index == 0) {
            return;
        }
        if (index > 0) {
            mHistoryAdapter.remove(index);
        }
        mHistoryAdapter.addData(0, key);
        int max = SettingUtils.getInstance().getSearchHistoryMaxCount();
        List<String> list = mHistoryAdapter.getData();
        if (list.size() > max) {
            mHistoryAdapter.remove(list.size() - 1);
        }
        RvScrollTopUtils.smoothScrollTop(rv_history);
        presenter.saveHistory(mHistoryAdapter.getData());
        changeHistoryVisible();
    }

    @Override
    public void getHotKeyListSuccess(int code, List<HotKeyBean> data) {
        mHotAdapter.setNewData(data);
    }

    @Override
    public void getHotKeyListFail(int code, String msg) {
    }
}
