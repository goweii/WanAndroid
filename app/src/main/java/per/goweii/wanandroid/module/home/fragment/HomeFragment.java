package per.goweii.wanandroid.module.home.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kennyc.view.MultiStateView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
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
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.anylayer.Layer;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.core.glide.transformation.BlurTransformation;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.ColorUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.cropimageview.CropImageView;
import per.goweii.statusbarcompat.utils.LuminanceUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.BannerAutoSwitchEnableEvent;
import per.goweii.wanandroid.event.CloseSecondFloorEvent;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.HomeActionBarEvent;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.module.book.fragment.BookFragment;
import per.goweii.wanandroid.module.home.activity.SearchActivity;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.home.presenter.HomePresenter;
import per.goweii.wanandroid.module.home.view.HomeView;
import per.goweii.wanandroid.module.main.activity.ScanActivity;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;
import per.goweii.wanandroid.module.main.dialog.WebDialog;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.module.main.model.RecommendBean;
import per.goweii.wanandroid.module.main.utils.BottomDrawerViewOutlineProvider;
import per.goweii.wanandroid.utils.ConfigUtils;
import per.goweii.wanandroid.utils.DarkModeUtils;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.MultiStateUtils;
import per.goweii.wanandroid.utils.RecommendManager;
import per.goweii.wanandroid.utils.RvScrollTopUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.utils.router.Router;
import per.goweii.wanandroid.widget.CollectView;
import per.goweii.wanandroid.widget.bottomdrawer.BottomDrawerLayout;
import per.goweii.wanandroid.widget.refresh.ShiciRefreshHeader;
import per.goweii.wanandroid.widget.refresh.SimpleOnMultiListener;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * GitHub: https://github.com/goweii
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements RvScrollTopUtils.ScrollTop, HomeView {

    private static final int PAGE_START = 0;

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.dl)
    BottomDrawerLayout dl;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fl_dl_second_floor)
    FrameLayout fl_dl_second_floor;
    @BindView(R.id.fl_dl_content)
    FrameLayout fl_dl_content;
    @BindView(R.id.v_dl_content_handle)
    FrameLayout v_dl_content_handle;
    @BindView(R.id.v_dl_content_mask)
    View v_dl_content_mask;
    @BindView(R.id.iv_second_floor_background)
    CropImageView iv_second_floor_background;

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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onHomeActionBarEvent(HomeActionBarEvent event) {
        event.removeSticky();
        updateActionBarByConfig(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseSecondFloorEvent(CloseSecondFloorEvent event) {
        dl.open(300);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBannerAutoSwitchEnableEvent(BannerAutoSwitchEnableEvent event) {
        if (mBanner == null) return;
        if (event.isEnable()) {
            if (dl.isOpened()) {
                mBanner.isAutoPlay(true);
                mBanner.startAutoPlay();
            }
        } else {
            mBanner.isAutoPlay(false);
            mBanner.stopAutoPlay();
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
        abc.setOnLeftIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ScanActivity.start(getActivity());
                }
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
        initSecondFloor();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArticleAdapter();
        mAdapter.setEnableLoadMore(false);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.getArticleList(currPage, false);
            }
        }, rv);
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
                ArticleBean item = mAdapter.getItem(position);
                if (item != null) {
                    if (v.isChecked()) {
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

    private BookFragment bookFragment = null;
    private OnBackPressedCallback onBackPressedCallback = null;
    private BottomDrawerViewOutlineProvider secondFloorOutlineProvider = null;
    private Animator abcAnim = null;

    private void initSecondFloor() {
        if (getActivity() == null) return;
        ShiciRefreshHeader shiciRefreshHeader = (ShiciRefreshHeader) srl.getRefreshHeader();
        if (shiciRefreshHeader != null) {
            shiciRefreshHeader.setColor(ResUtils.getThemeColor(getActivity(), R.attr.colorOnMainOrSurface));
        }
        Fragment fragment = getChildFragmentManager().findFragmentByTag(BookFragment.class.getName());
        if (fragment == null) {
            bookFragment = new BookFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.fl_dl_second_floor, bookFragment, BookFragment.class.getName());
            ft.commitAllowingStateLoss();
        } else {
            bookFragment = (BookFragment) fragment;
        }
        onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                dl.open(300);
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        View bottomBar = getActivity().getWindow().getDecorView().findViewById(R.id.fl_bottom_bar);
        fl_dl_second_floor.setPadding(0, 0, 0, (int) dl.getCloseHeight());
        iv_second_floor_background.setAutoMoveDuration(20_000);
        iv_second_floor_background.setSmoothMoveAnimDuration(0);
        iv_second_floor_background.setCropScale(1.3F);
        iv_second_floor_background.setCropType(CropImageView.CropType.CENTER);
        v_dl_content_mask.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                dl.open(300);
            }
        });
        dl.setEnable(true);
        dl.setDraggable(false);
        dl.onClosed(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (onBackPressedCallback != null) {
                    onBackPressedCallback.setEnabled(true);
                }
                v_dl_content_mask.setClickable(true);
                dl.setDraggable(true);
                iv_second_floor_background.setAutoMove(true);
                mBanner.isAutoPlay(false);
                mBanner.stopAutoPlay();
                return null;
            }
        });
        dl.onOpened(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (onBackPressedCallback != null) {
                    onBackPressedCallback.setEnabled(false);
                }
                v_dl_content_mask.setClickable(false);
                dl.setDraggable(false);
                if (bookFragment != null) {
                    getChildFragmentManager().beginTransaction()
                            .hide(bookFragment)
                            .commitAllowingStateLoss();
                }
                iv_second_floor_background.setAutoMove(false);
                mBanner.isAutoPlay(true);
                mBanner.startAutoPlay();
                return null;
            }
        });
        dl.onDragStart(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (mBanner != null) {
                    mBanner.isAutoPlay(false);
                    mBanner.stopAutoPlay();
                }
                return null;
            }
        });
        dl.onDragEnd(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        });
        secondFloorOutlineProvider = new BottomDrawerViewOutlineProvider(ResUtils.getDimens(R.dimen.round_radius));
        secondFloorOutlineProvider.setToView(fl_dl_content);
        dl.onDragging(new Function1<Float, Unit>() {
            @Override
            public Unit invoke(Float f) {
                bottomBar.setTranslationY(bottomBar.getHeight() * f);
                fl_dl_second_floor.setAlpha(f);
                final float minScale = 0.95F;
                float s = minScale + f * (1F - minScale);
                fl_dl_second_floor.setPivotX(fl_dl_second_floor.getWidth() * 0.5F);
                fl_dl_second_floor.setPivotY(fl_dl_second_floor.getHeight() * 0F);
                fl_dl_second_floor.setScaleX(s);
                fl_dl_second_floor.setScaleY(s);
                secondFloorOutlineProvider.updateFaction(f);
                final float bgScale = 1.3F;
                iv_second_floor_background.setCropScale(bgScale + (1F - f) * (1F - bgScale));
                if ((abcAnim == null || !abcAnim.isRunning())) {
                    abc.setTranslationY(-abc.getHeight() * f);
                }
                final float minAlpha = 0.0F;
                final float fromFaction = 0.6F;
                if (f < fromFaction) {
                    srl.setAlpha(1F);
                    v_dl_content_mask.setAlpha(0);
                    v_dl_content_handle.setAlpha(0);
                } else {
                    final float fa = (f - fromFaction) / (1 - fromFaction);
                    //srl.setAlpha(minAlpha + (1F - fa) * (1F - minAlpha));
                    v_dl_content_mask.setAlpha(fa);
                    v_dl_content_handle.setAlpha(fa);
                }
                if (f == 1F) {
                    srl.setVisibility(View.INVISIBLE);
                } else {
                    srl.setVisibility(View.VISIBLE);
                }
                return null;
            }
        });
        srl.setOnMultiListener(new SimpleOnMultiListener() {
            private boolean isSecondFloor = false;

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
                final float secondPercent = 1.5F;
                if (isDragging) {
                    if (percent > secondPercent) {
                        if (!isSecondFloor) {
                            isSecondFloor = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                srl.performHapticFeedback(HapticFeedbackConstants.GESTURE_START);
                            } else {
                                srl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                            }
                            if (abcAnim != null) {
                                abcAnim.cancel();
                            }
                            abcAnim = ObjectAnimator.ofFloat(abc, "translationY",
                                    abc.getTranslationY(), -abc.getHeight());
                            abcAnim.start();
                            if (shiciRefreshHeader != null) {
                                shiciRefreshHeader.setTextAndHideIcon("释放进入书籍教程");
                            }
                        }
                    } else {
                        if (isSecondFloor) {
                            isSecondFloor = false;
                            if (abcAnim != null) {
                                abcAnim.cancel();
                            }
                            abcAnim = ObjectAnimator.ofFloat(abc, "translationY",
                                    abc.getTranslationY(), 0);
                            abcAnim.start();
                            if (shiciRefreshHeader != null) {
                                shiciRefreshHeader.restoreToCurrState();
                            }
                        }
                    }
                }
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
                super.onHeaderReleased(header, headerHeight, maxDragHeight);
                if (isSecondFloor) {
                    isSecondFloor = false;
                    if (abcAnim != null) {
                        abcAnim.cancel();
                    }
                    abcAnim = ObjectAnimator.ofFloat(abc, "translationY",
                            abc.getTranslationY(), -abc.getHeight());
                    abcAnim.setDuration(300);
                    abcAnim.start();
                    srl.closeHeaderOrFooter();
                    srl.finishRefresh();
                    dl.close(300);
                    if (bookFragment != null) {
                        getChildFragmentManager().beginTransaction()
                                .show(bookFragment)
                                .commitAllowingStateLoss();
                    }
                }
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void showWebDialog(int position) {
        mWebDialog = WebDialog.create(getContext(), null, mAdapter.getData(), position);
        mWebDialog.setOnPageChangedListener(new WebDialog.OnPageChangedListener() {
            @Override
            public void onPageChanged(int pos, ArticleBean data) {
                int headerCount = mAdapter.getHeaderLayoutCount();
                int currItemPos = 0;
                List<ArticleBean> datas = mAdapter.getData();
                for (int i = 0; i < datas.size(); i++) {
                    ArticleBean bean = datas.get(i);
                    if (bean.getId() == data.getId()) {
                        currItemPos = headerCount + i;
                        break;
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
        mWebDialog.addOnDismissListener(new Layer.OnDismissListener() {
            @Override
            public void onPreDismiss(@NonNull Layer layer) {
            }

            @Override
            public void onPostDismiss(@NonNull Layer layer) {
                mWebDialog = null;
            }
        });
        mWebDialog.show();
    }

    @Override
    protected void loadData() {
        ConfigBean configBean = ConfigUtils.getInstance().getConfig();
        if (configBean.isEnableAtNow()) {
            updateActionBarByConfig(new HomeActionBarEvent(
                    configBean.getHomeTitle(),
                    configBean.getActionBarBgColor(),
                    configBean.getActionBarBgImageUrl(),
                    configBean.getSecondFloorBgImageUrl(),
                    configBean.getSecondFloorBgImageBlurPercent()
            ));
        } else {
            updateActionBarByConfig(new HomeActionBarEvent());
        }
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
        if (!isFirstVisible && abc != null) {
            abc.refreshStatusBarMode();
        }
        if (mBanner != null) {
            if (dl.isOpened()) {
                mBanner.isAutoPlay(true);
                mBanner.startAutoPlay();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dl.open(0);
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        if (mBanner != null) {
            mBanner.isAutoPlay(false);
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
            int screenWidth = DisplayInfoUtils.getInstance().getWidthPixels();
            int screenHeight = DisplayInfoUtils.getInstance().getHeightPixels();
            int height = (int) (Math.min(screenWidth, screenHeight) * (9F / 16F));
            mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            mBanner.setImageLoader(new ImageLoaderInterface<ImageView>() {
                @Override
                public ImageView createImageView(Context context) {
                    ImageView imageView = new ImageView(context);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return imageView;
                }

                @Override
                public void displayImage(Context context, Object data, ImageView imageView) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (data instanceof BannerBean) {
                        BannerBean bean = (BannerBean) data;
                        ImageLoader.banner(imageView, bean.getImagePath());
                    } else if (data instanceof RecommendBean.BannerBean) {
                        RecommendBean.BannerBean bean = (RecommendBean.BannerBean) data;
                        ImageLoader.banner(imageView, bean.getUrl());
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
                    } else if (obj instanceof RecommendBean.BannerBean) {
                        RecommendBean.BannerBean bean = (RecommendBean.BannerBean) obj;
                        Router.routeTo(bean.getRoute());
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

    private void removeTopItems() {
        List<ArticleBean> list = mAdapter.getData();
        int from = -1;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            ArticleBean bean = list.get(i);
            if (from < 0) {
                if (bean.isTop()) {
                    from = i;
                }
            }
            if (from >= 0) {
                if (!bean.isTop()) {
                    break;
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
        RecommendBean recommendBean = RecommendManager.getInstance().getBean();
        if (recommendBean != null && recommendBean.getBannerList() != null) {
            mBannerDatas.addAll(recommendBean.getBannerList());
        }
        mBannerDatas.addAll(data);
        mBanner.setImages(mBannerDatas);
        refreshBannerTitles();
        mBanner.start();
        MultiStateUtils.toContent(msv);

        if (recommendBean == null) {
            RecommendManager.getInstance().getBean(new RecommendManager.Callback() {
                @Override
                public void onResult(@Nullable RecommendBean recommendBean) {
                    if (mBannerDatas == null) {
                        mBannerDatas = new ArrayList<>();
                    }
                    mBannerDatas.clear();
                    if (recommendBean != null && recommendBean.getBannerList() != null) {
                        mBannerDatas.addAll(recommendBean.getBannerList());
                    }
                    mBannerDatas.addAll(data);
                    mBanner.setImages(mBannerDatas);
                    refreshBannerTitles();
                    mBanner.start();
                    MultiStateUtils.toContent(msv);
                }
            });
        }
    }

    @Override
    public void getBannerFail(int code, String msg) {
        RecommendManager.getInstance().getBean(new RecommendManager.Callback() {
            @Override
            public void onResult(@Nullable RecommendBean bean) {
                if (mBannerDatas == null) {
                    mBannerDatas = new ArrayList<>();
                }
                mBannerDatas.clear();
                if (bean != null && bean.getBannerList() != null) {
                    mBannerDatas.addAll(bean.getBannerList());
                }
                mBanner.setImages(mBannerDatas);
                refreshBannerTitles();
                mBanner.start();
                MultiStateUtils.toContent(msv);
            }
        });
    }

    private void refreshBannerTitles() {
        List<String> titles = new ArrayList<>(mBannerDatas.size());
        for (Object bean : mBannerDatas) {
            if (bean instanceof BannerBean) {
                titles.add(((BannerBean) bean).getTitle());
            } else if (bean instanceof RecommendBean.BannerBean) {
                titles.add(((RecommendBean.BannerBean) bean).getTitle());
            }
        }
        mBanner.setBannerTitles(titles);
    }

    @Override
    public void getArticleListSuccess(int code, ArticleListBean data) {
        currPage = data.getCurPage() + PAGE_START;
        if (data.getCurPage() == 1) {
            MultiStateUtils.toContent(msv);
            List<ArticleBean> newList = new ArrayList<>();
            List<ArticleBean> oldList = mAdapter.getData();
            for (ArticleBean bean : oldList) {
                if (bean.isTop()) {
                    newList.add(bean);
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
        List<ArticleBean> newList = new ArrayList<>(data);
        List<ArticleBean> oldList = mAdapter.getData();
        for (ArticleBean bean : oldList) {
            if (!bean.isTop()) {
                newList.add(bean);
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

    private void updateActionBarByConfig(@NonNull HomeActionBarEvent event) {
        if (abc != null) {
            if (event.getHomeTitle() == null) {
                abc.getTitleTextView().setText("首页");
            } else {
                abc.getTitleTextView().setText(event.getHomeTitle());
            }
            clearActionBarIconMode();
            changeActionBarBgColor(event.getActionBarBgColor());
            if (!TextUtils.isEmpty(event.getActionBarBgImageUrl())) {
                GlideHelper.with(getContext())
                        .asBitmap()
                        .load(event.getActionBarBgImageUrl())
                        .getBitmap(new SimpleCallback<Bitmap>() {
                            @Override
                            public void onResult(Bitmap data) {
                                if (abc != null) {
                                    abc.setBackground(new BitmapDrawable(data));
                                }
                            }
                        });
            }
        }
        if (iv_second_floor_background != null) {
            if (!TextUtils.isEmpty(event.getSecondFloorBgImageUrl())) {
                GlideHelper.with(getContext())
                        .asBitmap()
                        .load(event.getSecondFloorBgImageUrl())
                        .transformation(new BlurTransformation(event.getSecondFloorBgImageBlurPercent()))
                        .into(iv_second_floor_background);
            } else {
                setSecondFloorDefBgColor();
            }
        }
    }

    private void changeActionBarBgColor(@Nullable String colorStr) {
        if (!TextUtils.isEmpty(colorStr)) {
            try {
                int color = Color.parseColor(colorStr);
                abc.setBackgroundColor(color);
                changeActionBarMode(isColorLight(color));
                return;
            } catch (IllegalArgumentException ignore) {
            }
        }
        int color = ResUtils.getThemeColor(abc, R.attr.colorMainOrSurface);
        abc.setBackgroundColor(color);
    }

    private void setSecondFloorDefBgColor() {
        if (iv_second_floor_background == null) return;
        int color = ResUtils.getThemeColor(iv_second_floor_background, R.attr.colorMain);
        color = ColorUtils.changingColor(color, Color.BLACK, 0.5F);
        iv_second_floor_background.setImageDrawable(new ColorDrawable(color));
    }

    private boolean isColorLight(int color) {
        double lumi = LuminanceUtils.calcLuminance(color);
        Context context = getContext();
        if (context != null) {
            if (DarkModeUtils.isDarkMode(context)) {
                return lumi > 0.1F;
            }
        }
        return LuminanceUtils.isLight(lumi);
    }

    private void clearActionBarIconMode() {
        int color = ResUtils.getThemeColor(abc, R.attr.colorOnMainOrSurface);
        abc.getTitleTextView().setTextColor(color);
        abc.getLeftTextView().setTextColor(color);
        abc.getRightTextView().setTextColor(color);
        abc.getRightIconView().setColorFilter(color);
        abc.getLeftIconView().setColorFilter(color);
    }

    private void changeActionBarMode(boolean darkIcon) {
        int color;
        if (darkIcon) {
            color = ResUtils.getThemeColor(abc, R.attr.colorIconDark);
        } else {
            color = ResUtils.getThemeColor(abc, R.attr.colorIconLight);
        }
        abc.getTitleTextView().setTextColor(color);
        abc.getLeftTextView().setTextColor(color);
        abc.getRightTextView().setTextColor(color);
        abc.getRightIconView().setColorFilter(color);
        abc.getLeftIconView().setColorFilter(color);
    }
}
