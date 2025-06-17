package per.goweii.wanandroid.module.home.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

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
import per.goweii.wanandroid.databinding.FragmentSearchHistoryBinding;
import per.goweii.wanandroid.module.home.activity.SearchActivity;
import per.goweii.wanandroid.module.home.model.HotKeyBean;
import per.goweii.wanandroid.module.home.presenter.SearchHistoryPresenter;
import per.goweii.wanandroid.module.home.view.SearchHistoryView;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * GitHub: https://github.com/goweii
 */
public class SearchHistoryFragment extends BaseFragment<SearchHistoryPresenter, FragmentSearchHistoryBinding> implements SearchHistoryView {
    private BaseQuickAdapter<HotKeyBean, BaseViewHolder> mHotAdapter;
    private BaseQuickAdapter<String, BaseViewHolder> mHistoryAdapter;

    private boolean mRemoveMode = false;
    private boolean mRemoveModeChanging = false;

    public static SearchHistoryFragment create() {
        return new SearchHistoryFragment();
    }

    @Nullable
    @Override
    protected FragmentSearchHistoryBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSearchHistoryBinding.inflate(inflater, container, false);
    }

    @Nullable
    @Override
    protected SearchHistoryPresenter initPresenter() {
        return new SearchHistoryPresenter();
    }

    @Override
    protected void initView() {
        binding.rvHot.setNestedScrollingEnabled(false);
        binding.rvHot.setHasFixedSize(true);
        binding.rvHot.setLayoutManager(new FlexboxLayoutManager(getContext()));
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
        binding.rvHot.setAdapter(mHotAdapter);
        binding.rvHistory.setLayoutManager(new FlexboxLayoutManager(getContext()));
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
        binding.rvHistory.setAdapter(mHistoryAdapter);
    }

    @Override
    protected void loadData() {
        presenter.getHotKeyList();
        mHistoryAdapter.setNewData(presenter.getHistory());
        changeHistoryVisible();
    }

    private void changeHistoryVisible() {
        if (mHistoryAdapter == null) {
            binding.llHistory.setVisibility(View.GONE);
        } else {
            if (mHistoryAdapter.getData().isEmpty()) {
                binding.llHistory.setVisibility(View.GONE);
            } else {
                binding.llHistory.setVisibility(View.VISIBLE);
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
            binding.tvDown.setVisibility(View.VISIBLE);
            binding.tvClean.setVisibility(View.GONE);
        } else {
            binding.tvDown.setVisibility(View.GONE);
            binding.tvClean.setVisibility(View.VISIBLE);
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
        RvScrollTopUtils.smoothScrollTop(binding.rvHistory);
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
