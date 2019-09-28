package per.goweii.wanandroid.module.home.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.anylayer.Layer;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.home.activity.SearchActivity;
import per.goweii.wanandroid.module.home.adapter.HomeAdapter;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.home.model.HomeBean;
import per.goweii.wanandroid.module.home.presenter.HomePresenter;
import per.goweii.wanandroid.module.home.view.HomeView;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.main.dialog.WebDialog;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RvAnimUtils;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * QQ: 302833254
 * E-mail: goweii@163.com
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
    private HomeAdapter mAdapter;

    private int currPage = PAGE_START;
    private SmartRefreshUtils mSmartRefreshUtils;
    private List<BannerBean> mBannerBeans;
    private List<View> mHeaderTopItemViews;
    private List<ArticleBean> mHeaderTopItemBeans;
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
        List<ArticleBean> list = mAdapter.getData();
        for (int i = 0; i < list.size(); i++) {
            ArticleBean item = list.get(i);
            if (item.getId() == event.getArticleId()) {
                if (item.isCollect() != event.isCollect()) {
                    item.setCollect(event.isCollect());
                    mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
                }
                if (mWebDialog != null) {
                    mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
                }
                break;
            }
        }
        if (mHeaderTopItemViews != null && mHeaderTopItemViews.size() > 0) {
            for (int i = 0; i < mHeaderTopItemViews.size(); i++) {
                ArticleBean item = mHeaderTopItemBeans.get(i);
                if (item.getId() == event.getArticleId()) {
                    if (item.isCollect() != event.isCollect()) {
                        item.setCollect(event.isCollect());
                        View view = mHeaderTopItemViews.get(i);
                        bindHeaderTopItem(view, item);
                    }
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingChangeEvent(SettingChangeEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isShowTopChanged()) {
            if (SettingUtils.getInstance().isShowTop()) {
                if (mHeaderTopItemViews == null || mHeaderTopItemViews.size() == 0) {
                    presenter.getTopArticleList(true);
                }
            } else {
                removeHeaderTopItems();
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
            List<ArticleBean> list = mAdapter.getData();
            for (int i = 0; i < list.size(); i++) {
                ArticleBean item = list.get(i);
                if (item.isCollect()) {
                    item.setCollect(false);
                    mAdapter.notifyItemChanged(i + mAdapter.getHeaderLayoutCount());
                }
            }
            if (mHeaderTopItemViews != null && mHeaderTopItemViews.size() > 0) {
                for (int i = 0; i < mHeaderTopItemViews.size(); i++) {
                    ArticleBean item = mHeaderTopItemBeans.get(i);
                    if (item.isCollect()) {
                        item.setCollect(false);
                        View view = mHeaderTopItemViews.get(i);
                        bindHeaderTopItem(view, item);
                        break;
                    }
                }
            }
        }
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
        abc.setOnRightIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.start(getContext());
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
        mAdapter = new HomeAdapter();
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().getRvAnim());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currPage++;
                presenter.getArticleList(currPage, false);
            }
        }, rv);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBean item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getId(), item.getTitle(), item.getLink());
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showWebDialog(false, position);
                return true;
            }
        });
        mAdapter.setOnCollectViewClickListener(new HomeAdapter.OnCollectViewClickListener() {
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
        createHeaderBanner();
        rv.setAdapter(mAdapter);
        MultiStateUtils.setEmptyAndErrorClick(msv, new SimpleListener() {
            @Override
            public void onResult() {
                loadData();
            }
        });
    }

    private void showWebDialog(boolean header, int position) {
        int index = position;
        if (!header) {
            if (mHeaderTopItemBeans != null) {
                index += mHeaderTopItemBeans.size();
            }
        }
        mWebDialog = WebDialog.create(getContext(), mHeaderTopItemBeans, mAdapter.getData(), index);
        mWebDialog.setOnPageChangedListener(new WebDialog.OnPageChangedListener() {
            @Override
            public void onPageChanged(int pos, ArticleBean data) {
                int headerCount = mAdapter.getHeaderLayoutCount();
                int currItemPos = 0;
                boolean find = false;
                if (mHeaderTopItemBeans != null) {
                    for (int i = 0; i < mHeaderTopItemBeans.size(); i++) {
                        ArticleBean bean = mHeaderTopItemBeans.get(i);
                        if (bean.getId() == data.getId()) {
                            find = true;
                            break;
                        }
                    }
                }
                if (!find) {
                    List<ArticleBean> datas = mAdapter.getData();
                    for (int i = 0; i < datas.size(); i++) {
                        ArticleBean bean = datas.get(i);
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
        presenter.getConfig();
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
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
        if (mBanner == null) {
            mBanner = new Banner(getContext());
            int height = (int) (DisplayInfoUtils.getInstance().getWidthPixels() * (9F / 16F));
            mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            mBanner.setImageLoader(new com.youth.banner.loader.ImageLoader() {
                @Override
                public void displayImage(Context context, Object url, ImageView imageView) {
                    ImageLoader.banner(imageView, (String) url);
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
                    BannerBean bean = mBannerBeans.get(position);
                    WebActivity.start(getContext(), bean.getTitle(), bean.getUrl());
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
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_author = view.findViewById(R.id.tv_author);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_super_chapter_name = view.findViewById(R.id.tv_super_chapter_name);
        TextView tv_chapter_name = view.findViewById(R.id.tv_chapter_name);
        LinearLayout ll_new = view.findViewById(R.id.ll_new);
        TextView tv_new = view.findViewById(R.id.tv_new);
        ImageView iv_img = view.findViewById(R.id.iv_img);
        CollectView cv_collect = view.findViewById(R.id.cv_collect);
        TextView tv_tag = view.findViewById(R.id.tv_tag);
        tv_title.setText(item.getTitle());
        tv_author.setText(item.getAuthor());
        tv_time.setText(item.getNiceDate());
        tv_super_chapter_name.setText(item.getSuperChapterName());
        tv_chapter_name.setText(item.getChapterName());
        ll_new.setVisibility(View.VISIBLE);
        tv_new.setText("置顶");
        tv_new.setTextColor(ResUtils.getColor(R.color.text_accent));
        if (!TextUtils.isEmpty(item.getEnvelopePic())) {
            ImageLoader.image(iv_img, item.getEnvelopePic());
            iv_img.setVisibility(View.VISIBLE);
        } else {
            iv_img.setVisibility(View.GONE);
        }
        if (item.isCollect()) {
            cv_collect.setChecked(true);
        } else {
            cv_collect.setChecked(false);
        }
        if (item.getTags() != null && item.getTags().size() > 0) {
            tv_tag.setText(item.getTags().get(0).getName());
            tv_tag.setVisibility(View.VISIBLE);
        } else {
            tv_tag.setVisibility(View.GONE);
        }
        cv_collect.setOnClickListener(new CollectView.OnClickListener() {
            @Override
            public void onClick(CollectView v) {
                if (!v.isChecked()) {
                    presenter.collect(item, v);
                } else {
                    presenter.uncollect(item, v);
                }
            }
        });
        view.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                WebActivity.start(getContext(), item.getId(), item.getTitle(), item.getLink());
            }
        });
    }

    private void removeHeaderTopItems() {
        if (mHeaderTopItemViews != null) {
            for (View view : mHeaderTopItemViews) {
                mAdapter.removeHeaderView(view);
            }
            mHeaderTopItemViews.clear();
            mHeaderTopItemViews = null;
        }
        if (mHeaderTopItemBeans != null) {
            mHeaderTopItemBeans.clear();
            mHeaderTopItemBeans = null;
        }
    }

    private void createHeaderTopItems(List<ArticleBean> data) {
        removeHeaderTopItems();
        mHeaderTopItemBeans = data;
        if (mHeaderTopItemBeans == null || mHeaderTopItemBeans.isEmpty()) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mHeaderTopItemViews = new ArrayList<>();
        for (int i = 0; i < mHeaderTopItemBeans.size(); i++) {
            View view = inflater.inflate(R.layout.rv_item_article, rv, false);
            mHeaderTopItemViews.add(view);
        }
        for (int i = 0; i < mHeaderTopItemViews.size(); i++) {
            final int pos = i;
            View view = mHeaderTopItemViews.get(i);
            ArticleBean bean = mHeaderTopItemBeans.get(i);
            bindHeaderTopItem(view, bean);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showWebDialog(true, pos);
                    return true;
                }
            });
            mAdapter.addHeaderView(view, mAdapter.getHeaderLayout().getChildCount());
        }
    }

    @Override
    public void getBannerSuccess(int code, List<BannerBean> data) {
        mBannerBeans = data;
        List<String> urls = new ArrayList<>(data.size());
        List<String> titles = new ArrayList<>(data.size());
        for (BannerBean bean : data) {
            urls.add(bean.getImagePath());
            titles.add(bean.getTitle());
        }
        mBanner.setImages(urls);
        mBanner.setBannerTitles(titles);
        mBanner.start();
        MultiStateUtils.toContent(msv);
    }

    @Override
    public void getBannerFail(int code, String msg) {
    }

    @Override
    public void getArticleListSuccess(int code, HomeBean data) {
        if (currPage == PAGE_START) {
            MultiStateUtils.toContent(msv);
            mAdapter.setNewData(data.getDatas());
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
        createHeaderTopItems(data);
    }

    @Override
    public void getTopArticleListFailed(int code, String msg) {
    }

    @Override
    public void getConfigSuccess(ConfigBean configBean) {
        if (!TextUtils.isEmpty(configBean.getHomeTitle())) {
            abc.getTitleTextView().setText(configBean.getHomeTitle());
        }
        if (TextUtils.isEmpty(configBean.getActionBarBgImageUrl())) {
            if (TextUtils.isEmpty(configBean.getActionBarBgColor())) {
                abc.setBackgroundResource(R.color.main);
            } else {
                try {
                    int color = Color.parseColor(configBean.getActionBarBgColor());
                    abc.setBackgroundColor(color);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!TextUtils.isEmpty(configBean.getActionBarBgColor())) {
                try {
                    int color = Color.parseColor(configBean.getActionBarBgColor());
                    abc.setBackgroundColor(color);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            GlideHelper.with(getContext())
                    .load(configBean.getActionBarBgImageUrl())
                    .get(new SimpleCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap data) {
                            abc.setBackground(new BitmapDrawable(data));
                        }
                    });
        }
    }

    @Override
    public void allFail() {
        MultiStateUtils.toError(msv);
    }
}
