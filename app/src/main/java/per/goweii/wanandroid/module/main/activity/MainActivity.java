package per.goweii.wanandroid.module.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.anypermission.AnyPermission;
import per.goweii.anypermission.RequestInterceptor;
import per.goweii.anypermission.RequestListener;
import per.goweii.anypermission.RuntimeRequester;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.dialog.PermissionDialog;
import per.goweii.basic.ui.dialog.UpdateDialog;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.module.home.fragment.HomeFragment;
import per.goweii.wanandroid.module.main.dialog.DownloadDialog;
import per.goweii.wanandroid.module.main.fragment.KnowledgeNavigationFragment;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.main.presenter.MainPresenter;
import per.goweii.wanandroid.module.main.view.MainView;
import per.goweii.wanandroid.module.mine.fragment.MineFragment;
import per.goweii.wanandroid.module.project.fragment.ProjectFragment;
import per.goweii.wanandroid.module.wxarticle.fragment.WxFragment;
import per.goweii.wanandroid.utils.UpdateUtils;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView, ViewPager.OnPageChangeListener {

    private static final int REQ_CODE_PERMISSION = 1;

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.iv_bb_home)
    ImageView iv_bb_home;
    @BindView(R.id.tv_bb_home)
    TextView tv_bb_home;
    @BindView(R.id.iv_bb_knowledge)
    ImageView iv_bb_knowledge;
    @BindView(R.id.tv_bb_knowledge)
    TextView tv_bb_knowledge;
    @BindView(R.id.iv_bb_wechat)
    ImageView iv_bb_wechat;
    @BindView(R.id.tv_bb_wechat)
    TextView tv_bb_wechat;
    @BindView(R.id.iv_bb_project)
    ImageView iv_bb_project;
    @BindView(R.id.tv_bb_project)
    TextView tv_bb_project;
    @BindView(R.id.iv_bb_mine)
    ImageView iv_bb_mine;
    @BindView(R.id.tv_bb_mine)
    TextView tv_bb_mine;

    private FixedFragmentPagerAdapter mPagerAdapter;
    private RuntimeRequester mRuntimeRequester;
    private long lastClickTime = 0L;
    private int lastClickPos = 0;
    private UpdateUtils mUpdateUtils;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean swipeBackEnable() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Nullable
    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initView() {
        vp.addOnPageChangeListener(this);
        vp.setOffscreenPageLimit(4);
        mPagerAdapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mPagerAdapter);
        mPagerAdapter.setFragmentList(
                HomeFragment.create(),
                KnowledgeNavigationFragment.create(),
                WxFragment.create(),
                ProjectFragment.create(),
                MineFragment.create()
        );
        vp.setCurrentItem(0);
        onPageSelected(vp.getCurrentItem());
    }

    @Override
    protected void loadData() {
        presenter.update();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("MainActivity", "onCreate");
        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d("MainActivity", "onNewIntent");
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        String action = intent.getAction();
        LogUtils.d("MainActivity", "action=" + intent.getAction());
        if (action == null) {
            return;
        }
        switch (action) {
            default:
                break;
            case Intent.ACTION_VIEW:
                handleOpenUrl(intent);
                break;
            case Intent.ACTION_SEND:
                handleShareText(intent);
                break;
        }
    }

    private void handleOpenUrl(Intent intent) {
        Uri data = intent.getData();
        LogUtils.d("MainActivity", "data=" + data);
        if (data == null) {
            return;
        }
        String scheme = data.getScheme();
        if (!TextUtils.equals(scheme, "http") && !TextUtils.equals(scheme, "https")) {
            return;
        }
        WebActivity.start(getContext(), data.toString());
    }

    private void handleShareText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        LogUtils.d("MainActivity", "sharedText=" + sharedText);
        if (TextUtils.isEmpty(sharedText)) {
            return;
        }
        int urlStartIndex = sharedText.indexOf("https://");
        if (urlStartIndex < 0) {
            urlStartIndex = sharedText.indexOf("http://");
        }
        if (urlStartIndex < 0) {
            return;
        }
        StringBuilder urlBuilder = new StringBuilder();
        for (int i = urlStartIndex; i < sharedText.length(); i++) {
            char c = sharedText.charAt(i);
            if (c == ' ') {
                break;
            }
            urlBuilder.append(c);
        }
        String url = urlBuilder.toString();
        LogUtils.d("MainActivity", "sharedUrl=" + url);
        WebActivity.start(getContext(), url);
    }

    @OnClick({
            R.id.ll_bb_home, R.id.ll_bb_knowledge, R.id.ll_bb_wechat, R.id.ll_bb_project, R.id.ll_bb_mine
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected boolean onClick1(View v) {
        switch (v.getId()) {
            default:
                return false;
            case R.id.ll_bb_home:
                vp.setCurrentItem(0);
                break;
            case R.id.ll_bb_knowledge:
                vp.setCurrentItem(1);
                break;
            case R.id.ll_bb_wechat:
                vp.setCurrentItem(2);
                break;
            case R.id.ll_bb_project:
                vp.setCurrentItem(3);
                break;
            case R.id.ll_bb_mine:
                vp.setCurrentItem(4);
                break;
        }
        notifyScrollTop(vp.getCurrentItem());
        return true;
    }

    private void notifyScrollTop(int pos) {
        long currClickTime = System.currentTimeMillis();
        if (lastClickPos == pos && currClickTime - lastClickTime <= Config.SCROLL_TOP_DOUBLE_CLICK_DELAY) {
            Fragment fragment = mPagerAdapter.getItem(pos);
            if (fragment instanceof ScrollTop) {
                ScrollTop scrollTop = (ScrollTop) fragment;
                scrollTop.scrollTop();
            }
        }
        lastClickPos = pos;
        lastClickTime = currClickTime;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        iv_bb_home.setImageResource(R.drawable.ic_home_normal);
        iv_bb_knowledge.setImageResource(R.drawable.ic_book_normal);
        iv_bb_wechat.setImageResource(R.drawable.ic_wechat_normal);
        iv_bb_project.setImageResource(R.drawable.ic_project_normal);
        iv_bb_mine.setImageResource(R.drawable.ic_mine_normal);
        switch (i) {
            default:
                break;
            case 0:
                iv_bb_home.setImageResource(R.drawable.ic_home_selected);
                break;
            case 1:
                iv_bb_knowledge.setImageResource(R.drawable.ic_book_selected);
                break;
            case 2:
                iv_bb_wechat.setImageResource(R.drawable.ic_wechat_selected);
                break;
            case 3:
                iv_bb_project.setImageResource(R.drawable.ic_project_selected);
                break;
            case 4:
                iv_bb_mine.setImageResource(R.drawable.ic_mine_selected);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void updateSuccess(int code, UpdateBean data) {
        mUpdateUtils = UpdateUtils.newInstance();
        if (!mUpdateUtils.shouldUpdate(data.getVersion_code())) {
            return;
        }
        UpdateDialog.with(getContext())
                .setUrl(data.getUrl())
                .setVersionCode(data.getVersion_code())
                .setVersionName(data.getVersion_name())
                .setForce(data.isForce())
                .setDescription(data.getDesc())
                .setTime(data.getTime())
                .setOnUpdateListener(new UpdateDialog.OnUpdateListener() {
                    @Override
                    public void onDownload(String url, boolean isForce) {
                        download(data.getVersion_name(), url, isForce);
                    }

                    @Override
                    public void onIgnore(int versionCode) {
                        mUpdateUtils.ignore(versionCode);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mRuntimeRequester != null) {
            mRuntimeRequester.onActivityResult(requestCode);
        }
    }

    private void download(final String versionName, final String url, final boolean isForce) {
        mRuntimeRequester = AnyPermission.with(getContext())
                .runtime(REQ_CODE_PERMISSION)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .onBeforeRequest(new RequestInterceptor<String>() {
                    @Override
                    public void intercept(@NonNull String data, @NonNull Executor executor) {
                        PermissionDialog.with(getContext())
                                .setGoSetting(false)
                                .setGroupType(PermissionDialog.GroupType.STORAGE)
                                .setOnNextListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.execute();
                                    }
                                })
                                .setOnCloseListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .onBeenDenied(new RequestInterceptor<String>() {
                    @Override
                    public void intercept(@NonNull String data, @NonNull Executor executor) {
                        PermissionDialog.with(getContext())
                                .setGoSetting(false)
                                .setGroupType(PermissionDialog.GroupType.STORAGE)
                                .setOnNextListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.execute();
                                    }
                                })
                                .setOnCloseListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .onGoSetting(new RequestInterceptor<String>() {
                    @Override
                    public void intercept(@NonNull String data, @NonNull Executor executor) {
                        PermissionDialog.with(getContext())
                                .setGoSetting(true)
                                .setGroupType(PermissionDialog.GroupType.STORAGE)
                                .setOnNextListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.execute();
                                    }
                                })
                                .setOnCloseListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .request(new RequestListener() {
                    @Override
                    public void onSuccess() {
                        DownloadDialog.with(getActivity(), isForce, url, versionName);
                    }

                    @Override
                    public void onFailed() {
                    }
                });
    }
}
