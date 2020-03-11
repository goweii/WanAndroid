package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.basic.utils.IntentUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.adapter.ReadLaterAdapter;
import per.goweii.wanandroid.module.mine.model.ReadLaterEntity;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RealmHelper;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class ReadLaterActivity extends BaseActivity {

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private ReadLaterAdapter mAdapter;

    private int perPageCount = 20;
    private int currPage = 0;
    private RealmHelper mRealmHelper;

    public static void start(Context context) {
        Intent intent = new Intent(context, ReadLaterActivity.class);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingChangeEvent(SettingChangeEvent event) {
        if (isDestroyed()) {
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
        return R.layout.activity_read_later;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = 0;
                getPageList();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ReadLaterAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currPage++;
                getPageList();
            }
        }, rv);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.closeAll(null);
                ReadLaterEntity item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                switch (view.getId()) {
                    default:
                        break;
                    case R.id.rl_top:
                        WebActivity.start(getContext(), item.getTitle(), item.getLink());
                        break;
                    case R.id.tv_copy:
                        CopyUtils.copyText(item.getLink());
                        ToastMaker.showShort("复制成功");
                        break;
                    case R.id.tv_open:
                        if (TextUtils.isEmpty(item.getLink())) {
                            ToastMaker.showShort("链接为空");
                            break;
                        }
                        if (getContext() != null) {
                            IntentUtils.openBrowser(getContext(), item.getLink());
                        }
                        break;
                    case R.id.tv_delete:
                        mRealmHelper.delete(item.getLink());
                        mAdapter.remove(position);
                        if (mAdapter.getData().isEmpty()) {
                            MultiStateUtils.toEmpty(msv);
                        } else {
                            MultiStateUtils.toContent(msv);
                        }
                        break;
                }
            }
        });
        rv.setAdapter(mAdapter);

        mRealmHelper = RealmHelper.create();
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        currPage = 0;
        getPageList();
    }

    @Override
    protected void onDestroy() {
        if (mRealmHelper != null) {
            mRealmHelper.destroy();
        }
        super.onDestroy();
    }

    public void getPageList() {
        List<ReadLaterEntity> list = mRealmHelper.get(currPage, perPageCount);
        if (currPage == 0) {
            mAdapter.setNewData(list);
            if (list.isEmpty()) {
                MultiStateUtils.toEmpty(msv);
            } else {
                MultiStateUtils.toContent(msv);
            }
        } else {
            mAdapter.addData(list);
        }
        if (list.size() < perPageCount) {
            mAdapter.loadMoreEnd();
        }
        mSmartRefreshUtils.success();
    }
}
