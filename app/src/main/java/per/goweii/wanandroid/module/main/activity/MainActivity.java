package per.goweii.wanandroid.module.main.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import per.goweii.basic.utils.SPUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.module.home.fragment.HomeFragment;
import per.goweii.wanandroid.module.main.dialog.CopiedLinkDialog;
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
    @BindView(R.id.ll_bb)
    LinearLayout ll_bb;
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

    private String mLastCopyLink = "";
    private CopiedLinkDialog mCopiedLinkDialog = null;

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
        LogUtils.i("MainActivity", "MainActivity started");
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
        mLastCopyLink = SPUtils.getInstance().get("LastCopyLink", "");
    }

    @Override
    protected void loadData() {
        presenter.update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ll_bb.postDelayed(new Runnable() {
            @Override
            public void run() {
                isNeedOpenLink();
            }
        }, 500L);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void isNeedOpenLink() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboardManager.getPrimaryClip();
        if (clip == null || clip.getItemCount() <= 0) {
            return;
        }
        for (int i = 0; i < clip.getItemCount(); i++) {
            ClipData.Item item = clip.getItemAt(i);
            LogUtils.i("WanApp", "" + item.toString());
        }
        ClipData.Item item = clip.getItemAt(0);
        if (TextUtils.isEmpty(item.getText())) {
            return;
        }
        String text = item.getText().toString();
        if (TextUtils.equals(mLastCopyLink, text)) {
            return;
        }
        Uri uri = Uri.parse(text);
        if (!TextUtils.equals(uri.getScheme(), "http") && !TextUtils.equals(uri.getScheme(), "https")) {
            return;
        }
        if (mCopiedLinkDialog == null) {
            mCopiedLinkDialog = new CopiedLinkDialog(ll_bb, text, new SimpleListener() {
                @Override
                public void onResult() {
                    mLastCopyLink = text;
                    SPUtils.getInstance().save("LastCopyLink", mLastCopyLink);
                }
            });
        }
        if (!mCopiedLinkDialog.isShow()) {
            mCopiedLinkDialog.show();
        }
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
        iv_bb_home.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_knowledge.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_wechat.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_project.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        iv_bb_mine.setColorFilter(ContextCompat.getColor(getContext(), R.color.third));
        switch (i) {
            default:
                break;
            case 0:
                iv_bb_home.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 1:
                iv_bb_knowledge.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 2:
                iv_bb_wechat.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 3:
                iv_bb_project.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
                break;
            case 4:
                iv_bb_mine.setColorFilter(ContextCompat.getColor(getContext(), R.color.main));
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
                .setUrlBackup(data.getUrl_backup())
                .setVersionCode(data.getVersion_code())
                .setVersionName(data.getVersion_name())
                .setForce(data.isForce())
                .setDescription(data.getDesc())
                .setTime(data.getTime())
                .setOnUpdateListener(new UpdateDialog.OnUpdateListener() {
                    @Override
                    public void onDownload(String url, String urlBackup, boolean isForce) {
                        download(data.getVersion_name(), url, urlBackup, isForce);
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

    private void download(final String versionName, final String url, final String urlBackup, final boolean isForce) {
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
                        DownloadDialog.with(getActivity(), isForce, url, urlBackup, versionName);
                    }

                    @Override
                    public void onFailed() {
                    }
                });
    }
}
