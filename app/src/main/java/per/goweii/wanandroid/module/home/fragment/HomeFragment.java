package per.goweii.wanandroid.module.home.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.kennyc.view.MultiStateView;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoaderInterface;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.anylayer.Layer;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.basic.utils.ext.ViewExtKt;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.HomeActionBarEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.home.activity.SearchActivity;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.home.presenter.HomePresenter;
import per.goweii.wanandroid.module.home.view.HomeView;
import per.goweii.wanandroid.module.main.activity.ScanActivity;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;
import per.goweii.wanandroid.module.main.dialog.WebDialog;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.TM;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.utils.ad.AdForBannerFactory;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * GitHub: https://github.com/goweii
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements ScrollTop, HomeView {

    private static final int PAGE_START = 0;

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Banner mBanner;
    private ArticleAdapter mAdapter;

    private int currPage = PAGE_START;
    private SmartRefreshUtils mSmartRefreshUtils;
    private List<Object> mBannerDatas;
    private WebDialog mWebDialog;

    public static HomeFragment create() {
        return new HomeFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionEvent(CollectionEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.getArticleId() == -1) {
            return;
        }
        mAdapter.forEach(new ArticleAdapter.ArticleForEach() {
            @Override
            public boolean forEach(int dataPos, int adapterPos, ArticleBean bean) {
                if (bean.getId() == event.getArticleId()) {
                    if (bean.isCollect() != event.isCollect()) {
                        bean.setCollect(event.isCollect());
                        mAdapter.notifyItemChanged(adapterPos);
                    }
                    if (mWebDialog != null) {
                        mAdapter.notifyItemChanged(adapterPos);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingChangeEvent(SettingChangeEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isShowTopChanged()) {
            if (SettingUtils.getInstance().isShowTop()) {
                presenter.getTopArticleList(true);
            } else {
                removeTopItems();
            }
        }
        if (event.isShowBannerChanged()) {
            createHeaderBanner();
            if (SettingUtils.getInstance().isShowBanner()) {
                presenter.getBanner();
            }
        }
        if (event.isRvAnimChanged()) {
            RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isLogin()) {
            if (SettingUtils.getInstance().isShowTop()) {
                presenter.getTopArticleList(true);
            }
            currPage = PAGE_START;
            presenter.getArticleList(currPage, true);
        } else {
            mAdapter.notifyAllUnCollect();
        }
    }

    private HomeActionBarEvent mHomeActionBarEvent = null;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onHomeActionBarEvent(HomeActionBarEvent event) {
        event.removeSticky();
        LogUtils.d("HomeFragment", "onHomeActionBarEvent");
        if (isDetached()) {
            return;
        }
        if (event.getHomeTitle() != null) {
            if (mHomeActionBarEvent == null || !TextUtils.equals(mHomeActionBarEvent.getHomeTitle(), event.getHomeTitle())) {
                abc.getTitleTextView().setText(event.getHomeTitle());
            }
        } else {
            abc.getTitleTextView().setText("首页");
        }
        if (TextUtils.isEmpty(event.getActionBarBgImageUrl())) {
            if (TextUtils.isEmpty(event.getActionBarBgColor())) {
                abc.setBackgroundResource(R.color.basic_ui_action_bar_bg);
            } else {
                if (mHomeActionBarEvent == null || !TextUtils.equals(mHomeActionBarEvent.getActionBarBgColor(), event.getActionBarBgColor())) {
                    try {
                        int color = Color.parseColor(event.getActionBarBgColor());
                        abc.setBackgroundColor(color);
                    } catch (IllegalArgumentException e) {
                        abc.setBackgroundResource(R.color.basic_ui_action_bar_bg);
                    }
                }
            }
        } else {
            if (mHomeActionBarEvent == null || !TextUtils.equals(mHomeActionBarEvent.getActionBarBgImageUrl(), event.getActionBarBgImageUrl())) {
                if (TextUtils.isEmpty(event.getActionBarBgColor())) {
                    abc.setBackgroundResource(R.color.basic_ui_action_bar_bg);
                } else {
                    if (mHomeActionBarEvent == null || !TextUtils.equals(mHomeActionBarEvent.getActionBarBgColor(), event.getActionBarBgColor())) {
                        try {
                            int color = Color.parseColor(event.getActionBarBgColor());
                            abc.setBackgroundColor(color);
                        } catch (IllegalArgumentException e) {
                            abc.setBackgroundResource(R.color.basic_ui_action_bar_bg);
                        }
                    }
                }
                GlideHelper.with(getContext())
                        .asBitmap()
                        .load(event.getActionBarBgImageUrl())
                        .getBitmap(new SimpleCallback<Bitmap>() {
                            @Override
                            public void onResult(Bitmap data) {
                                abc.setBackground(new BitmapDrawable(data));
                            }
                        });
            }
        }
        mHomeActionBarEvent = event;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Nullable
    @Override
    protected HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected void initView() {
        TM.APP_STARTUP.record("HomeFragment initView");
        abc.setOnRightIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.start(getContext());
            }
        });
        abc.setOnLeftIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                ScanActivity.start(v.getContext());
            }
        });
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
            @Override
            public void onRefresh() {
                currPage = PAGE_START;
                if (SettingUtils.getInstance().isShowBanner()) {
                    presenter.getBanner();
                }
                if (SettingUtils.getInstance().isShowTop()) {
                    presenter.getTopArticleList(true);
                }
                presenter.getArticleList(currPage, true);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArticleAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getArticleList(currPage, false);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBean item = mAdapter.getArticleBean(position);
                if (item != null) {
                    UrlOpenUtils.Companion.with(item).open(getContext());
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showWebDialog(position);
                return true;
            }
        });
        mAdapter.setOnItemChildViewClickListener(new ArticleAdapter.OnItemChildViewClickListener() {
            @Override
            public void onCollectClick(BaseViewHolder helper, CollectView v, int position) {
                ArticleBean item = mAdapter.getArticleBean(position);
                if (item != null) {
                    if (!v.isChecked()) {
                        presenter.collect(item, v);
                    } else {
                        presenter.uncollect(item, v);
                    }
                }
            }
        });
        createHeaderBanner();
        rv.setAdapter(mAdapter);
        MultiStateUtils.setEmptyAndErrorClick(msv, new SimpleListener() {
            @Override
            public void onResult() {
                loadData();
            }
        });
        mAdForBannerFactory = AdForBannerFactory.create(getContext(), new AdForBannerFactory.OnADLoadedListener() {
            @Override
            public void onLoaded(@NonNull NativeExpressADView adView) {
                if (mBanner != null && mBanner.getVisibility() == View.VISIBLE) {
                    if (mBannerDatas != null) {
                        boolean nonAd = true;
                        for (int i = mBannerDatas.size() - 1; i >= 0; i--) {
                            Object obj = mBannerDatas.get(i);
                            if (obj instanceof NativeExpressADView) {
                                nonAd = false;
                            }
                        }
                        if (nonAd) {
                            if (mBannerDatas.size() > 0) {
                                mBannerDatas.add(1, adView);
                            } else {
                                mBannerDatas.add(0, adView);
                            }
                            mBanner.setImages(mBannerDatas);
                            refreshBannerTitles();
                            mBanner.start();
                        }
                    }
                }
            }
        });
    }

    private AdForBannerFactory mAdForBannerFactory = null;

    @Override
    public void onDestroyView() {
        if (mAdForBannerFactory != null) {
            mAdForBannerFactory.destroy();
        }
        super.onDestroyView();
    }

    private void showWebDialog(int position) {
        mWebDialog = WebDialog.create(getContext(), null, mAdapter.getData(), position);
        mWebDialog.setOnPageChangedListener(new WebDialog.OnPageChangedListener() {
            @Override
            public void onPageChanged(int pos, ArticleBean data) {
                int headerCount = mAdapter.getHeaderLayoutCount();
                int currItemPos = 0;
                List<MultiItemEntity> datas = mAdapter.getData();
                for (int i = 0; i < datas.size(); i++) {
                    MultiItemEntity entity = datas.get(i);
                    if (entity.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
                        ArticleBean bean = (ArticleBean) entity;
                        if (bean.getId() == data.getId()) {
                            currItemPos = headerCount + i;
                            break;
                        }
                    }
                }
                if (currItemPos < 0) {
                    currItemPos = 0;
                }
                if (currItemPos > mAdapter.getItemCount() - 1) {
                    currItemPos = mAdapter.getItemCount() - 1;
                }
                rv.smoothScrollToPosition(currItemPos);
            }
        });
        mWebDialog.onDismissListener(new Layer.OnDismissListener() {
            @Override
            public void onDismissing(Layer layer) {
            }

            @Override
            public void onDismissed(Layer layer) {
                mWebDialog = null;
            }
        });
        mWebDialog.show();
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        if (SettingUtils.getInstance().isShowBanner()) {
            presenter.getBanner();
        }
        if (SettingUtils.getInstance().isShowTop()) {
            presenter.getTopArticleList(false);
        }
        currPage = PAGE_START;
        presenter.getArticleList(currPage, false);
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        TM.APP_STARTUP.record("HomeFragment onVisible");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBanner != null) {
            mBanner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }

    @Override
    public void scrollTop() {
        if (isAdded() && !isDetached()) {
            RvScrollTopUtils.smoothScrollTop(rv);
        }
    }

    private void createHeaderBanner() {
        if (mBanner == null && getContext() != null) {
            mBanner = new Banner(getContext());
            int height = (int) (DisplayInfoUtils.getInstance().getWidthPixels() * (9F / 16F));
            mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            mBanner.setImageLoader(new ImageLoaderInterface<FrameLayout>() {
                @Override
                public FrameLayout createImageView(Context context) {
                    FrameLayout container = new FrameLayout(context);
                    container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return container;
                }

                @Override
                public void displayImage(Context context, Object data, FrameLayout container) {
                    if (data instanceof BannerBean) {
                        ImageView imageView = new ImageView(context);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        if (container.getChildCount() > 0) {
                            container.removeAllViews();
                        }
                        container.addView(imageView);
                        ViewExtKt.onPreDraw(imageView, new Function1<View, Unit>() {
                            @Override
                            public Unit invoke(View view) {
                                TM.APP_STARTUP.end("HomeFragment Banner onPreDraw");
                                return null;
                            }
                        });
                        ImageLoader.banner(imageView, ((BannerBean) data).getImagePath());
                    } else if (data instanceof NativeExpressADView) {
                        NativeExpressADView adView = (NativeExpressADView) data;
                        if (container.getChildCount() > 0) {
                            container.removeAllViews();
                        }
                        if (adView.getParent() != null) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }
                        container.addView(adView);
                        adView.render();
                    }
                }
            });
            mBanner.setIndicatorGravity(BannerConfig.CENTER);
            mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
            mBanner.setBannerAnimation(Transformer.Default);
            mBanner.startAutoPlay();
            mBanner.setDelayTime(5000);
            mBanner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    Object obj = mBannerDatas.get(position);
                    if (obj instanceof BannerBean) {
                        BannerBean bean = (BannerBean) obj;
                        UrlOpenUtils.Companion
                                .with(bean.getUrl())
                                .title(bean.getTitle())
                                .open(getContext());
                    }
                }
            });
            mAdapter.addHeaderView(mBanner, 0);
        }
        if (SettingUtils.getInstance().isShowBanner()) {
            mBanner.setVisibility(View.VISIBLE);
        } else {
            mBanner.setVisibility(View.GONE);
        }
    }

    private void bindHeaderTopItem(View view, ArticleBean item) {
        ArticleAdapter.bindArticle(view, item, new ArticleAdapter.OnCollectListener() {
            @Override
            public void collect(ArticleBean item, CollectView v) {
                presenter.collect(item, v);
            }

            @Override
            public void uncollect(ArticleBean item, CollectView v) {
                presenter.uncollect(item, v);
            }
        });
    }

    private void removeTopItems() {
        List<MultiItemEntity> list = mAdapter.getData();
        int from = -1;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            MultiItemEntity entity = list.get(i);
            if (from < 0) {
                if (entity.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
                    ArticleBean bean = (ArticleBean) entity;
                    if (bean.isTop()) {
                        from = i;
                    }
                }
            }
            if (from >= 0) {
                if (entity.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
                    ArticleBean bean = (ArticleBean) entity;
                    if (!bean.isTop()) {
                        break;
                    }
                }
                count++;
            }
        }
        if (from >= 0) {
            for (int i = 0; i < count; i++) {
                mAdapter.remove(from);
            }
        }
    }

    @Override
    public void getBannerSuccess(int code, List<BannerBean> data) {
        if (mBannerDatas == null) {
            mBannerDatas = new ArrayList<>();
        }
        mBannerDatas.clear();
        mBannerDatas.addAll(data);
        if (mAdForBannerFactory != null) {
            NativeExpressADView adView = mAdForBannerFactory.getADView();
            if (adView != null) {
                if (mBannerDatas.size() > 0) {
                    mBannerDatas.add(1, adView);
                } else {
                    mBannerDatas.add(0, adView);
                }
            }
        }
        mBanner.setImages(mBannerDatas);
        refreshBannerTitles();
        mBanner.start();
        MultiStateUtils.toContent(msv);
    }

    private void refreshBannerTitles() {
        List<String> titles = new ArrayList<>(mBannerDatas.size());
        for (Object bean : mBannerDatas) {
            if (bean instanceof BannerBean) {
                titles.add(((BannerBean) bean).getTitle());
            } else {
                titles.add("");
            }
        }
        mBanner.setBannerTitles(titles);
    }

    @Override
    public void getBannerFail(int code, String msg) {
    }

    @Override
    public void getArticleListSuccess(int code, ArticleListBean data) {
        currPage = data.getCurPage() + PAGE_START;
        if (data.getCurPage() == 1) {
            MultiStateUtils.toContent(msv);
            List<MultiItemEntity> newList = new ArrayList<>();
            List<MultiItemEntity> oldList = mAdapter.getData();
            for (MultiItemEntity entity : oldList) {
                if (entity.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
                    ArticleBean bean = (ArticleBean) entity;
                    if (bean.isTop()) {
                        newList.add(bean);
                    }
                }
            }
            newList.addAll(data.getDatas());
            mAdapter.setNewData(newList);
        } else {
            mAdapter.addData(data.getDatas());
            if (mWebDialog != null) {
                mWebDialog.notifyDataSetChanged();
            }
            mAdapter.loadMoreComplete();
        }
        if (data.isOver()) {
            mAdapter.loadMoreEnd();
        } else {
            if (!mAdapter.isLoadMoreEnable()) {
                mAdapter.setEnableLoadMore(true);
            }
        }
        mSmartRefreshUtils.success();
    }

    @Override
    public void getArticleListFailed(int code, String msg) {
        ToastMaker.showShort(msg);
        mSmartRefreshUtils.fail();
        mAdapter.loadMoreFail();
    }

    @Override
    public void getTopArticleListSuccess(int code, List<ArticleBean> data) {
        MultiStateUtils.toContent(msv);
        for (ArticleBean bean : data) {
            bean.setTop(true);
        }
        List<MultiItemEntity> newList = new ArrayList<>(data);
        List<MultiItemEntity> oldList = mAdapter.getData();
        for (MultiItemEntity entity : oldList) {
            if (entity.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
                ArticleBean bean = (ArticleBean) entity;
                if (!bean.isTop()) {
                    newList.add(bean);
                }
            }
        }
        mAdapter.setNewData(newList);
    }

    @Override
    public void getTopArticleListFailed(int code, String msg) {
    }

    @Override
    public void allFail() {
        MultiStateUtils.toError(msv);
    }
}
