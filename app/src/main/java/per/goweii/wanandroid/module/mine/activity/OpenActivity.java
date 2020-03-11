package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.model.OpenEntity;
import per.goweii.wanandroid.utils.MultiStateUtils;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class OpenActivity extends BaseActivity {

    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private BaseQuickAdapter<OpenEntity, BaseViewHolder> mAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, OpenActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BaseQuickAdapter<OpenEntity, BaseViewHolder>(R.layout.rv_item_open) {
            @Override
            protected void convert(BaseViewHolder helper, OpenEntity item) {
                helper.setText(R.id.tv_project, item.getProject());
                if (TextUtils.isEmpty(item.getDescription())) {
                    helper.setGone(R.id.tv_description, false);
                } else {
                    helper.setGone(R.id.tv_description, true);
                    helper.setText(R.id.tv_description, item.getDescription());
                }
            }
        };
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OpenEntity item = mAdapter.getItem(position);
                if (item != null) {
                    WebActivity.start(getContext(), item.getProject(), item.getLink());
                }
            }
        });
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        MultiStateUtils.toLoading(msv);
        List<OpenEntity> list = new ArrayList<>();
        list.add(new OpenEntity("goweii/RxHttp", "对RxJava2+Retrofit2+OkHttp3的封装", "https://github.com/goweii/RxHttp"));
        list.add(new OpenEntity("goweii/SwipeBack", "Android Activity滑动返回。支持4个方向，支持下层Activity联动和自定义动效", "https://github.com/goweii/SwipeBack"));
        list.add(new OpenEntity("goweii/LazyFragment", "懒加载Fragment，提供可见和隐藏的回调，支持在ViewPager中多重嵌套，支持support和androidx", "https://github.com/goweii/LazyFragment"));
        list.add(new OpenEntity("goweii/ActionBarEx", "高拓展高自定义性ActionBar，完美替代Android系统默认", "https://github.com/goweii/ActionBarEx"));
        list.add(new OpenEntity("goweii/AnyLayer", "用于替代Android自带Dialog和PopupWindow", "https://github.com/goweii/AnyLayer"));
        list.add(new OpenEntity("goweii/AnyDialog", "Android高定制性，高易用性Dialog。", "https://github.com/goweii/AnyDialog"));
        list.add(new OpenEntity("goweii/RevealLayout", "揭示效果布局，可以指定2个子布局，以圆形揭示效果切换选中状态", "https://github.com/goweii/RevealLayout"));
        // list.add(new OpenEntity("goweii/AnyView", "快速将一个布局封装成一个自定义View", "https://github.com/goweii/AnyView"));
        list.add(new OpenEntity("goweii/PercentImageView", "自由指定宽高比的ImageView", "https://github.com/goweii/PercentImageView"));
        list.add(new OpenEntity("goweii/Blurred", "Android高斯模糊库", "https://github.com/goweii/Blurred"));
        list.add(new OpenEntity("goweii/AnyPermission", "Android权限申请（运行时权限、未知应用安装权限、悬浮窗权限、显示通知和访问通知权限）", "https://github.com/goweii/AnyPermission"));
        list.add(new OpenEntity("goweii/KeyboardCompat", "Android软键盘兼容库，支持开启关闭监听和EditText获取焦点时布局上移", "https://github.com/goweii/KeyboardCompat"));
        list.add(new OpenEntity("goweii/PictureSelector", "forked from LuckSiege/PictureSelector. Picture Selector Library for Android  多图片及音视频选择器", "https://github.com/goweii/PictureSelector"));
        list.add(new OpenEntity("JakeWharton/butterknife", "Bind Android views and callbacks to fields and methods.", "https://github.com/JakeWharton/butterknife"));
        list.add(new OpenEntity("greenrobot/EventBus", "Event bus for Android and Java that simplifies communication between Activities, Fragments, Threads, Services, etc. Less code, better quality.", "https://github.com/greenrobot/EventBus"));
        // list.add(new OpenEntity("Justson/AgentWeb", "AgentWeb is a powerful library based on Android WebView.", "https://github.com/Justson/AgentWeb"));
        list.add(new OpenEntity("google/gson", "A Java serialization/deserialization library to convert Java Objects into JSON and back", "https://github.com/google/gson"));
        list.add(new OpenEntity("franmontiel/PersistentCookieJar", "A persistent CookieJar implementation for OkHttp 3 based on SharedPreferences.", "https://github.com/franmontiel/PersistentCookieJar"));
        list.add(new OpenEntity("bumptech/glide", "An image loading and caching library for Android focused on smooth scrolling", "https://github.com/bumptech/glide"));
        list.add(new OpenEntity("CymChad/BaseRecyclerViewAdapterHelper", "BRVAH:Powerful and flexible RecyclerAdapter", "https://github.com/CymChad/BaseRecyclerViewAdapterHelper"));
        list.add(new OpenEntity("scwang90/SmartRefreshLayout", "下拉刷新、上拉加载、二级刷新、淘宝二楼、RefreshLayout、OverScroll，Android智能下拉刷新框架，支持越界回弹、越界拖动，具有极强的扩展性，集成了几十种炫酷的Header和 Footer。", "https://github.com/scwang90/SmartRefreshLayout"));
        list.add(new OpenEntity("vinc3m1/RoundedImageView", "A fast ImageView that supports rounded corners, ovals, and circles.", "https://github.com/vinc3m1/RoundedImageView"));
        list.add(new OpenEntity("hdodenhof/CircleImageView", "A circular ImageView for Android", "https://github.com/hdodenhof/CircleImageView"));
        list.add(new OpenEntity("hackware1993/MagicIndicator", "A powerful, customizable and extensible ViewPager indicator framework. As the best alternative of ViewPagerIndicator, TabLayout and PagerSlidingTabStrip —— 强大、可定制、易扩展的 ViewPager 指示器框架。是ViewPagerIndicator、TabLayout、PagerSlidingTabStrip的最佳替代品。支持角标，更支持在非ViewPager场景下使用（使用hide()、show()切换Fragment或使用setVisibility切换FrameLayout里的View等）", "https://github.com/hackware1993/MagicIndicator"));
        // list.add(new OpenEntity("chrisbanes/PhotoView", "Implementation of ImageView for Android that supports zooming, by various touch gestures.", "https://github.com/chrisbanes/PhotoView"));
        list.add(new OpenEntity("zhanghai/MaterialProgressBar", "Material Design ProgressBar with consistent appearance", "https://github.com/zhanghai/MaterialProgressBar"));
        list.add(new OpenEntity("google/flexbox-layout", "Flexbox for Android", "https://github.com/google/flexbox-layout"));
        list.add(new OpenEntity("youth5201314/banner", "Android广告图片轮播控件，支持无限循环和多种主题，可以灵活设置轮播样式、动画、轮播和切换时间、位置、图片加载框架等！", "https://github.com/youth5201314/banner"));
        list.add(new OpenEntity("mmin18/RealtimeBlurView", "A realtime blurring overlay for Android (like iOS UIVisualEffectView)", "https://github.com/mmin18/RealtimeBlurView"));
        list.add(new OpenEntity("Kennyc1012/MultiStateView", "Android View that displays different content based on its state", "https://github.com/Kennyc1012/MultiStateView"));
        list.add(new OpenEntity("JakeWharton/DiskLruCache", "Java implementation of a Disk-based LRU cache which specifically targets Android compatibility.", "https://github.com/JakeWharton/DiskLruCache"));
        list.add(new OpenEntity("daimajia/AndroidSwipeLayout", "The Most Powerful Swipe Layout!", "https://github.com/daimajia/AndroidSwipeLayout"));
        mAdapter.setNewData(list);
        MultiStateUtils.toContent(msv);
    }
}
