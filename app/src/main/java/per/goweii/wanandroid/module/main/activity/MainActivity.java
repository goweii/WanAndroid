package per.goweii.wanandroid.module.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.anypermission.RequestListener;
import per.goweii.anypermission.RuntimeRequester;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.permission.PermissionUtils;
import per.goweii.basic.ui.dialog.UpdateDialog;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.HomeActionBarEvent;
import per.goweii.wanandroid.module.main.dialog.CopiedLinkDialog;
import per.goweii.wanandroid.module.main.dialog.DownloadDialog;
import per.goweii.wanandroid.module.main.dialog.PasswordDialog;
import per.goweii.wanandroid.module.main.dialog.PrivacyPolicyDialog;
import per.goweii.wanandroid.module.main.fragment.MainFragment;
import per.goweii.wanandroid.module.main.fragment.UserArticleFragment;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.main.presenter.MainPresenter;
import per.goweii.wanandroid.module.main.view.MainView;
import per.goweii.wanandroid.utils.CopiedTextProcessor;
import per.goweii.wanandroid.utils.TaskQueen;
import per.goweii.wanandroid.utils.UpdateUtils;
import per.goweii.wanandroid.utils.wanpwd.WanPwdParser;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView, ViewPager.OnPageChangeListener {

    private static final int REQ_CODE_PERMISSION = 1;

    @BindView(R.id.vp)
    ViewPager vp;

    private FixedFragmentPagerAdapter mPagerAdapter;
    private RuntimeRequester mRuntimeRequester;
    private UpdateUtils mUpdateUtils;
    private CopiedLinkDialog mCopiedLinkDialog = null;
    private PasswordDialog mPasswordDialog = null;

    private TaskQueen mTaskQueen = new TaskQueen();

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean swipeBackEnable() {
        return false;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTheme(R.style.AppTheme);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
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
        //showSplashDialog();
        vp.addOnPageChangeListener(this);
        vp.setOffscreenPageLimit(1);
        mPagerAdapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mPagerAdapter);
        mPagerAdapter.setFragmentList(UserArticleFragment.create(), MainFragment.create());
        vp.setCurrentItem(1);
        onPageSelected(vp.getCurrentItem());
        initCopiedTextProcessor();
        showPrivacyPolicyDialog();
    }

    private void showSplashDialog() {
        AnyLayer.dialog(this)
                .contentView(R.layout.activity_splash)
                .show(false);
    }

    private void initCopiedTextProcessor() {
        CopiedTextProcessor.getInstance().setProcessCallback(new CopiedTextProcessor.ProcessCallback() {
            @Override
            public void isLink(String link) {
                showLinkDialog(link);
            }

            @Override
            public void isPassword(WanPwdParser pwd) {
                showPasswordDialog(pwd);
            }
        });
    }

    private void showPrivacyPolicyDialog() {
        mTaskQueen.append(new TaskQueen.Task() {
            @Override
            public void run() {
                PrivacyPolicyDialog.showIfFirst(getContext(), new PrivacyPolicyDialog.CompleteCallback() {
                    @Override
                    public void onComplete() {
                        complete();
                    }
                });
            }
        });
    }

    @Override
    protected void loadData() {
        presenter.getConfig();
        presenter.update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vp.postDelayed(new Runnable() {
            @Override
            public void run() {
                CopiedTextProcessor.getInstance().process();
            }
        }, 500L);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void showLinkDialog(String link) {
        if (mCopiedLinkDialog != null) {
            if (mCopiedLinkDialog.isShow()) {
                if (!TextUtils.equals(mCopiedLinkDialog.getLink(), link)) {
                    mCopiedLinkDialog.dismiss();
                    mCopiedLinkDialog = null;
                }
            } else {
                if (!TextUtils.equals(mCopiedLinkDialog.getLink(), link)) {
                    mCopiedLinkDialog = null;
                } else {
                    mCopiedLinkDialog.show();
                }
            }
        }
        if (mCopiedLinkDialog == null) {
            mCopiedLinkDialog = new CopiedLinkDialog(vp, link);
            mCopiedLinkDialog.show();
        }
    }

    private void showPasswordDialog(WanPwdParser parser) {
        if (mPasswordDialog != null) {
            if (mPasswordDialog.isShow()) {
                if (parser.equals(mPasswordDialog.getPassword())) {
                    return;
                }
            }
        }
        mTaskQueen.append(new TaskQueen.Task(-10) {
            @Override
            public void run() {
                mPasswordDialog = new PasswordDialog(getContext(), parser);
                mPasswordDialog.onDismissListener(new Layer.OnDismissListener() {
                    @Override
                    public void onDismissing(Layer layer) {
                    }

                    @Override
                    public void onDismissed(Layer layer) {
                        mPasswordDialog = null;
                        complete();
                    }
                });
                mPasswordDialog.show();
            }
        });
    }

    public void openUserArticle() {
        vp.setCurrentItem(0);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mRuntimeRequester != null) {
            mRuntimeRequester.onActivityResult(requestCode);
        }
    }

    @Override
    public void onBackPressed() {
        if (vp.getCurrentItem() == 1) {
            super.onBackPressed();
        } else {
            vp.setCurrentItem(1);
        }
    }

    private void download(final String versionName, final String url, final String urlBackup, final boolean isForce) {
        mTaskQueen.append(new TaskQueen.Task() {
            @Override
            public void run() {
                mRuntimeRequester = PermissionUtils.request(new RequestListener() {
                    @Override
                    public void onSuccess() {
                        DownloadDialog.with(getActivity(), isForce, url, urlBackup, versionName, new DownloadDialog.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                complete();
                            }
                        });
                    }

                    @Override
                    public void onFailed() {
                        complete();
                    }
                }, getContext(), REQ_CODE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
    }

    @Override
    public void updateSuccess(int code, UpdateBean data) {
        mUpdateUtils = UpdateUtils.newInstance();
        if (!mUpdateUtils.shouldUpdate(data.getVersion_code())) {
            return;
        }
        mTaskQueen.append(new TaskQueen.Task() {
            @Override
            public void run() {
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
                        .setOnDismissListener(new UpdateDialog.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                complete();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void getConfigSuccess(ConfigBean configBean) {
        new HomeActionBarEvent(
                configBean.getHomeTitle(),
                configBean.getActionBarBgColor(),
                configBean.getActionBarBgImageUrl()
        ).postSticky();
    }
}
