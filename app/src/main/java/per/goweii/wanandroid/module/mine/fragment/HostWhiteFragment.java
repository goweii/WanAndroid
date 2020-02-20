package per.goweii.wanandroid.module.mine.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.mine.adapter.HostInterruptAdapter;
import per.goweii.wanandroid.module.mine.dialog.AddHostDialog;
import per.goweii.wanandroid.module.mine.model.HostEntity;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class HostWhiteFragment extends BaseFragment implements ScrollTop {

    @BindView(R.id.rv)
    RecyclerView rv;

    private HostInterruptAdapter mAdapter = null;

    public static HostWhiteFragment create() {
        return new HostWhiteFragment();
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
        return R.layout.fragment_host_interrupt;
    }

    @Nullable
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HostInterruptAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.remove(position);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.getData().get(position).setEnable(!mAdapter.getData().get(position).isEnable());
                mAdapter.notifyItemChanged(position);
            }
        });
        mAdapter.setOnCheckedChangeListener(new HostInterruptAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int position, boolean isChecked) {
                mAdapter.getData().get(position).setEnable(isChecked);
            }
        });
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.rv_item_host_interrupt_footer, null);
        footer.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                AddHostDialog.show(getContext(), new SimpleCallback<String>() {
                    @Override
                    public void onResult(String data) {
                        mAdapter.addData(new HostEntity(data, true));
                    }
                });
            }
        });
        mAdapter.addFooterView(footer);
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        List<HostEntity> list = SettingUtils.getInstance().getHostWhiteIntercept();
        List<HostEntity> newList = new ArrayList<>(list);
        mAdapter.setNewData(newList);
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        SettingUtils.getInstance().setHostWhiteIntercept(mAdapter.getData());
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }
}
