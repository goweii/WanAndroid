package per.goweii.wanandroid.module.mine.fragment;

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
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.dialog.ListDialog;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.basic.utils.IntentUtils;
import per.goweii.basic.utils.ToastMaker;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.mine.adapter.CollectionLinkAdapter;
import per.goweii.wanandroid.module.mine.dialog.EditCollectLinkDialog;
import per.goweii.wanandroid.module.mine.presenter.CollectionLinkPresenter;
import per.goweii.wanandroid.module.mine.view.CollectionLinkView;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionLinkFragment extends BaseFragment<CollectionLinkPresenter> implements ScrollTop, CollectionLinkView {

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private SmartRefreshUtils mSmartRefreshUtils;
    private CollectionLinkAdapter mAdapter;

    public static CollectionLinkFragment create() {
        return new CollectionLinkFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isCollect()) {
            presenter.getCollectLinkList(true);
        } else {
            if (event.getCollectId() != -1) {
                List<CollectionLinkBean> list = mAdapter.getData();
                for (int i = 0; i < list.size(); i++) {
                    CollectionLinkBean item = list.get(i);
                    if (item.getId() == event.getCollectId()) {
                        mAdapter.remove(i);
                        break;
                    }
                }
            }
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

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection_link;
    }

    @Nullable
    @Override
    protected CollectionLinkPresenter initPresenter() {
        return new CollectionLinkPresenter();
    }

    @Override
    protected void initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getCollectLinkList(true);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CollectionLinkAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CollectionLinkBean item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getName(), item.getLink());
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final CollectionLinkBean item = mAdapter.getItem(position);
                if (item == null) {
                    return true;
                }
                ListDialog.with(getContext())
                        .datas("编辑", "删除", "复制链接", "浏览器打开")
                        .noBtn()
                        .listener(new ListDialog.OnItemSelectedListener() {
                            @Override
                            public void onSelect(String data, int pos) {
                                switch (pos) {
                                    default:
                                        break;
                                    case 0:
                                        EditCollectLinkDialog.show(getContext(), item, new SimpleCallback<CollectionLinkBean>() {
                                            @Override
                                            public void onResult(CollectionLinkBean data) {
                                                presenter.updateCollectLink(data);
                                            }
                                        });
                                        break;
                                    case 1:
                                        presenter.uncollectLink(item);
                                        break;
                                    case 2:
                                        CopyUtils.copyText(item.getLink());
                                        ToastMaker.showShort("复制成功");
                                        break;
                                    case 3:
                                        if (TextUtils.isEmpty(item.getLink())) {
                                            ToastMaker.showShort("链接为空");
                                            break;
                                        }
                                        IntentUtils.openBrowser(getContext(), item.getLink());
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CollectionLinkBean item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                switch (view.getId()) {
                    default:
                        break;
                    case R.id.iv_remove:
                        presenter.uncollectLink(item);
                        break;
                }
            }
        });
        rv.setAdapter(mAdapter);
        MultiStateUtils.setEmptyAndErrorClick(msv, new SimpleListener() {
            @Override
            public void onResult() {
                loadData();
            }
        });
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        presenter.getCollectLinkList(false);
    }

    @Override
    public void getCollectLinkListSuccess(int code, List<CollectionLinkBean> data) {
        mAdapter.setNewData(data);
        mSmartRefreshUtils.success();
        if (data == null || data.isEmpty()) {
            MultiStateUtils.toEmpty(msv);
        } else {
            MultiStateUtils.toContent(msv);
        }
    }

    @Override
    public void getCollectLinkListFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        MultiStateUtils.toError(msv);
    }

    @Override
    public void updateCollectLinkSuccess(int code, CollectionLinkBean data) {
        List<CollectionLinkBean> list = mAdapter.getData();
        for (int i = 0; i < list.size(); i++) {
            CollectionLinkBean bean = list.get(i);
            if (bean.getId() == data.getId()) {
                bean.setName(data.getName());
                bean.setLink(data.getLink());
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }
}
