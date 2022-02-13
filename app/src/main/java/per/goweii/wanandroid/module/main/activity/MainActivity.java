package per.goweii.wanandroid.module.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.notification.NotificationLayer;
import per.goweii.anypermission.RequestListener;
import per.goweii.anypermission.RuntimeRequester;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.permission.PermissionUtils;
import per.goweii.basic.ui.dialog.UpdateDialog;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.db.model.ReadLaterModel;
import per.goweii.wanandroid.event.BannerAutoSwitchEnableEvent;
import per.goweii.wanandroid.event.CloseSecondFloorEvent;
import per.goweii.wanandroid.event.HomeActionBarEvent;
import per.goweii.wanandroid.module.main.dialog.AdvertDialog;
import per.goweii.wanandroid.module.main.dialog.CopiedLinkDialog;
import per.goweii.wanandroid.module.main.dialog.DownloadDialog;
import per.goweii.wanandroid.module.main.dialog.PasswordDialog;
import per.goweii.wanandroid.module.main.dialog.PrivacyPolicyDialog;
import per.goweii.wanandroid.module.main.fragment.MainFragment;
import per.goweii.wanandroid.module.main.fragment.UserArticleFragment;
import per.goweii.wanandroid.module.main.model.AdvertBean;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.main.presenter.MainPresenter;
import per.goweii.wanandroid.module.main.view.MainView;
import per.goweii.wanandroid.utils.ADUtils;
import per.goweii.wanandroid.utils.ConfigUtils;
import per.goweii.wanandroid.utils.CopiedTextProcessor;
import per.goweii.wanandroid.utils.PredefinedTaskQueen;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.ThemeUtils;
import per.goweii.wanandroid.utils.UpdateUtils;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.wanpwd.WanPwdParser;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    private static final int REQ_CODE_PERMISSION = 1;

    private static final String sTaskPrivacyPolicy = "PrivacyPolicy";
    private static final String sTaskUpdate = "Update";
    private static final String sTaskBetaUpdate = "BetaUpdate";
    private static final String sTaskDownload = "Download";
    private static final String sTaskAdvert = "Advert";
    private static final String sTaskWanPwd = "WanPwd";
    private static final String sTaskCopiedLink = "CopiedLink";
    private static final String sTaskReadLater = "ReadLater";

    @BindView(R.id.vp)
    ViewPager vp;

    private FixedFragmentPagerAdapter mPagerAdapter;
    private RuntimeRequester mRuntimeRequester;
    private UpdateUtils mUpdateUtils;
    private CopiedLinkDialog mCopiedLinkDialog = null;
    private PasswordDialog mPasswordDialog = null;

    private final PredefinedTaskQueen mPredefinedTaskQueen = new PredefinedTaskQueen();

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            LogUtils.d("ThemeUtils", "MainActivity onCreate setNotInstall");
            ThemeUtils.setNotInstall();
        }
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setBackgroundDrawable(new ColorDrawable(ResUtils.getThemeColor(this, R.attr.colorBackground)));
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
        vp.setOffscreenPageLimit(1);
        mPagerAdapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mPagerAdapter);
        mPagerAdapter.setFragmentList(UserArticleFragment.create(), MainFragment.create());
        vp.setCurrentItem(1);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                new CloseSecondFloorEvent().post();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskPrivacyPolicy));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskUpdate));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskBetaUpdate));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskDownload));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskAdvert));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskWanPwd));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskCopiedLink));
        mPredefinedTaskQueen.append(new PredefinedTaskQueen.Task(sTaskReadLater));
        mPredefinedTaskQueen.start();

        initCopiedTextProcessor();
        showPrivacyPolicyDialog();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                new BannerAutoSwitchEnableEvent(false).post();
                break;
            case MotionEvent.ACTION_UP:
                new BannerAutoSwitchEnableEvent(true).post();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initCopiedTextProcessor() {
        CopiedTextProcessor.getInstance().setProcessCallback(new CopiedTextProcessor.ProcessCallback() {
            @Override
            public void isLink(String link) {
                showCopiedLinkDialog(link);
                mPredefinedTaskQueen.get(sTaskWanPwd).complete();
            }

            @Override
            public void isWanPwd(WanPwdParser pwd) {
                showWanPwdDialog(pwd);
                mPredefinedTaskQueen.get(sTaskCopiedLink).complete();
            }

            @Override
            public void ignored() {
                mPredefinedTaskQueen.get(sTaskCopiedLink).complete();
                mPredefinedTaskQueen.get(sTaskWanPwd).complete();
            }
        });
    }

    private void showPrivacyPolicyDialog() {
        mPredefinedTaskQueen.get(sTaskPrivacyPolicy)
                .runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
                    @Override
                    public Unit invoke(PredefinedTaskQueen.Completion completion) {
                        PrivacyPolicyDialog.showIfFirst(getContext(), new PrivacyPolicyDialog.CompleteCallback() {
                            @Override
                            public void onComplete() {
                                completion.complete();
                            }
                        });
                        return null;
                    }
                });
    }

    @Override
    protected void loadData() {
        mUpdateUtils = UpdateUtils.newInstance();
        presenter.getConfig();
        presenter.getAdvert();
        presenter.update();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.getReadLaterArticle();
            }
        }, 500L);
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
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            if (!ThemeUtils.isWillInstall()) {
                ThemeUtils.updateLauncher(getApplicationContext(), ConfigUtils.getInstance().getThemeName());
            }
        }
    }

    private void showCopiedLinkDialog(String link) {
        // 先隐藏之前显示的不同的并且置空
        if (mCopiedLinkDialog != null) {
            if (!TextUtils.equals(mCopiedLinkDialog.getLink(), link)) {
                mCopiedLinkDialog.dismiss();
                mCopiedLinkDialog = null;
            }
        }
        // 没有复用的就重新创建
        if (mCopiedLinkDialog == null) {
            mCopiedLinkDialog = new CopiedLinkDialog(vp, link);
        }
        // 预定义任务是否完成？
        if (!mPredefinedTaskQueen.isCompleted()) {
            PredefinedTaskQueen.Task task = mPredefinedTaskQueen.get(sTaskCopiedLink);
            // 当前任务是否完成？
            if (!task.isCompleted()) {
                // 当前任务没有完成，更新任务
                task.runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
                    @Override
                    public Unit invoke(PredefinedTaskQueen.Completion completion) {
                        mCopiedLinkDialog.addOnDismissListener(new Layer.OnDismissListener() {
                            @Override
                            public void onPreDismiss(@NonNull Layer layer) {
                            }

                            @Override
                            public void onPostDismiss(@NonNull Layer layer) {
                                completion.complete();
                            }
                        });
                        mCopiedLinkDialog.show();
                        return null;
                    }
                });
                return;
            }
        }
        // 当前任务没有已完成，直接显示
        mCopiedLinkDialog.show();
    }

    private void showWanPwdDialog(WanPwdParser parser) {
        // 先隐藏之前显示的不同的并且置空
        if (mPasswordDialog != null) {
            if (!parser.equals(mPasswordDialog.getPassword())) {
                mPasswordDialog.dismiss();
                mPasswordDialog = null;
            }
        }
        // 没有复用的就重新创建
        if (mPasswordDialog == null) {
            mPasswordDialog = new PasswordDialog(getContext(), parser);
        }
        // 预定义任务是否完成？
        if (!mPredefinedTaskQueen.isCompleted()) {
            PredefinedTaskQueen.Task task = mPredefinedTaskQueen.get(sTaskWanPwd);
            // 当前任务是否完成？
            if (!task.isCompleted()) {
                // 当前任务没有完成，更新任务
                task.runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
                    @Override
                    public Unit invoke(PredefinedTaskQueen.Completion completion) {
                        mPasswordDialog.addOnDismissListener(new Layer.OnDismissListener() {
                            @Override
                            public void onPreDismiss(@NonNull Layer layer) {
                            }

                            @Override
                            public void onPostDismiss(@NonNull Layer layer) {
                                mPasswordDialog = null;
                                completion.complete();
                            }
                        });
                        mPasswordDialog.show();
                        return null;
                    }
                });
                return;
            }
        }
        // 当前任务没有已完成，直接显示
        mPasswordDialog.show();
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

    @Override
    public void updateSuccess(int code, UpdateBean data) {
        PredefinedTaskQueen.Task task = mPredefinedTaskQueen.get(sTaskUpdate);
        task.runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
            @Override
            public Unit invoke(PredefinedTaskQueen.Completion completion) {
                boolean shouldForce = mUpdateUtils.shouldForceUpdate(data);
                if (shouldForce || mUpdateUtils.shouldUpdate(data)) {
                    mPredefinedTaskQueen.get(sTaskBetaUpdate).complete();
                    UpdateDialog.with(getContext())
                            .setUrl(data.getUrl())
                            .setUrlBackup(data.getUrl_backup())
                            .setVersionCode(data.getVersion_code())
                            .setVersionName(data.getVersion_name())
                            .setForce(shouldForce)
                            .setDescription(data.getDesc())
                            .setTime(data.getTime())
                            .setOnUpdateListener(new UpdateDialog.OnUpdateListener() {
                                @Override
                                public void onDownload(String url, String urlBackup, boolean isForce) {
                                    download(url, urlBackup, isForce);
                                }

                                @Override
                                public void onIgnore(String versionName, int versionCode) {
                                    mUpdateUtils.ignore(versionCode);
                                }
                            })
                            .setOnDismissListener(new UpdateDialog.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    completion.complete();
                                    if (!mPredefinedTaskQueen.get(sTaskDownload).isRunnable()) {
                                        mPredefinedTaskQueen.get(sTaskDownload).complete();
                                    }
                                }
                            })
                            .show();
                } else {
                    if (!mUpdateUtils.isNewest(data) && UserUtils.getInstance().isLogin()) {
                        presenter.betaUpdate();
                    } else {
                        mPredefinedTaskQueen.get(sTaskBetaUpdate).complete();
                        mPredefinedTaskQueen.get(sTaskDownload).complete();
                    }
                    completion.complete();
                }
                return null;
            }
        });
    }

    @Override
    public void updateFailed(int code, String msg) {
        mPredefinedTaskQueen.get(sTaskUpdate).complete();
        mPredefinedTaskQueen.get(sTaskBetaUpdate).complete();
        mPredefinedTaskQueen.get(sTaskDownload).complete();
    }

    @Override
    public void betaUpdateSuccess(int code, UpdateBean data) {
        PredefinedTaskQueen.Task task = mPredefinedTaskQueen.get(sTaskBetaUpdate);
        task.runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
            @Override
            public Unit invoke(PredefinedTaskQueen.Completion completion) {
                boolean shouldForce = mUpdateUtils.shouldForceUpdate(data);
                if (shouldForce || mUpdateUtils.shouldUpdateBeta(data)) {
                    UpdateDialog.with(getContext())
                            .setTest(true)
                            .setUrl(data.getUrl())
                            .setUrlBackup(data.getUrl_backup())
                            .setVersionCode(data.getVersion_code())
                            .setVersionName(data.getVersion_name())
                            .setForce(shouldForce)
                            .setDescription(data.getDesc())
                            .setTime(data.getTime())
                            .setOnUpdateListener(new UpdateDialog.OnUpdateListener() {
                                @Override
                                public void onDownload(String url, String urlBackup, boolean isForce) {
                                    download(url, urlBackup, isForce);
                                }

                                @Override
                                public void onIgnore(String versionName, int versionCode) {
                                    mUpdateUtils.ignoreBeta(versionName, versionCode);
                                }
                            })
                            .setOnDismissListener(new UpdateDialog.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    completion.complete();
                                    if (!mPredefinedTaskQueen.get(sTaskDownload).isRunnable()) {
                                        mPredefinedTaskQueen.get(sTaskDownload).complete();
                                    }
                                }
                            })
                            .show();
                } else {
                    completion.complete();
                    mPredefinedTaskQueen.get(sTaskDownload).complete();
                }
                return null;
            }
        });
    }

    @Override
    public void betaUpdateFailed(int code, String msg) {
        mPredefinedTaskQueen.get(sTaskBetaUpdate).complete();
        mPredefinedTaskQueen.get(sTaskDownload).complete();
    }

    private void download(final String url, final String urlBackup, final boolean isForce) {
        PredefinedTaskQueen.Task task = mPredefinedTaskQueen.get(sTaskDownload);
        task.runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
            @Override
            public Unit invoke(PredefinedTaskQueen.Completion completion) {
                mRuntimeRequester = PermissionUtils.request(new RequestListener() {
                    @Override
                    public void onSuccess() {
                        DownloadDialog.with(getActivity(), isForce, url, urlBackup, new DownloadDialog.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                completion.complete();
                            }
                        });
                    }

                    @Override
                    public void onFailed() {
                        if (isForce) {
                            download(url, urlBackup, true);
                        } else {
                            completion.complete();
                        }
                    }
                }, getContext(), REQ_CODE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                return null;
            }
        });
    }

    @Override
    public void getConfigSuccess(ConfigBean configBean) {
        if (configBean.isEnableAtNow()) {
            new HomeActionBarEvent(
                    configBean.getHomeTitle(),
                    configBean.getActionBarBgColor(),
                    configBean.getActionBarBgImageUrl(),
                    configBean.getSecondFloorBgImageUrl(),
                    configBean.getSecondFloorBgImageBlurPercent()
            ).postSticky();
        } else {
            new HomeActionBarEvent().postSticky();
        }
    }

    @Override
    public void getConfigFailed(int code, String msg) {
    }

    @Override
    public void newThemeFounded() {
        WanApp.recreate();
    }

    @Override
    public void getAdvertSuccess(int code, AdvertBean advertBean) {
        if (!ADUtils.getInstance().shouldShowAD(advertBean)) {
            mPredefinedTaskQueen.get(sTaskAdvert).complete();
            return;
        }
        mPredefinedTaskQueen.get(sTaskAdvert)
                .runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
                    @Override
                    public Unit invoke(PredefinedTaskQueen.Completion completion) {
                        new AdvertDialog(MainActivity.this)
                                .setAdvertBean(advertBean)
                                .addOnVisibleChangeListener(new Layer.OnVisibleChangedListener() {
                                    @Override
                                    public void onShow(@NonNull Layer layer) {
                                        ADUtils.getInstance().setAdShown();
                                    }

                                    @Override
                                    public void onDismiss(@NonNull Layer layer) {
                                        completion.complete();
                                    }
                                })
                                .show();
                        return null;
                    }
                });
    }

    @Override
    public void getAdvertFailed(int code, String msg) {
        mPredefinedTaskQueen.get(sTaskAdvert).complete();
    }

    @Override
    public void getReadLaterArticleSuccess(ReadLaterModel readLaterModel) {
        mPredefinedTaskQueen.get(sTaskReadLater)
                .runnable(new Function1<PredefinedTaskQueen.Completion, Unit>() {
                    @Override
                    public Unit invoke(PredefinedTaskQueen.Completion completion) {
                        if (!SettingUtils.getInstance().isShowReadLaterNotification()) {
                            completion.complete();
                            return null;
                        }
                        new NotificationLayer(MainActivity.this)
                                .setContentView(R.layout.dialog_read_later_notification)
                                .addOnBindDataListener(new Layer.OnBindDataListener() {
                                    @Override
                                    public void onBindData(@NonNull Layer layer) {
                                        View child = layer.requireView(R.id.dialog_read_later_notification);
                                        child.setPadding(0, DisplayInfoUtils.getInstance().getStatusBarHeight(), 0, 0);
                                        TextView tv_title = layer.requireView(R.id.dialog_read_later_notification_tv_title);
                                        TextView tv_desc = layer.requireView(R.id.dialog_read_later_notification_tv_desc);
                                        tv_title.setText("是否继续阅读？");
                                        tv_desc.setText(readLaterModel.getTitle());
                                    }
                                })
                                .addOnClickToDismissListener(new Layer.OnClickListener() {
                                    @Override
                                    public void onClick(@NonNull Layer layer, @NonNull View view) {
                                        UrlOpenUtils.Companion
                                                .with(readLaterModel.getLink())
                                                .open(getContext());
                                    }
                                }, R.id.dialog_read_later_notification_ll_content)
                                .addOnDismissListener(new Layer.OnDismissListener() {
                                    @Override
                                    public void onPreDismiss(@NonNull Layer layer) {
                                    }

                                    @Override
                                    public void onPostDismiss(@NonNull Layer layer) {
                                        completion.complete();
                                    }
                                }).show();
                        return null;
                    }
                });
    }

    @Override
    public void getReadLaterArticleFailed() {
        mPredefinedTaskQueen.get(sTaskReadLater).complete();
    }
}
