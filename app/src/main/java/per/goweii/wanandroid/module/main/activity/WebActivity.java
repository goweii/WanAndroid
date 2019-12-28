package per.goweii.wanandroid.module.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarEx;
import per.goweii.anylayer.Layer;
import per.goweii.anypermission.RequestListener;
import per.goweii.anypermission.RuntimeRequester;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.permission.PermissionUtils;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.IntentUtils;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.ShareUtils;
import per.goweii.basic.utils.coder.MD5Coder;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.dialog.QrcodeShareDialog;
import per.goweii.wanandroid.module.main.dialog.WebGuideDialog;
import per.goweii.wanandroid.module.main.dialog.WebMenuDialog;
import per.goweii.wanandroid.module.main.dialog.WebQuickDialog;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.CollectArticleEntity;
import per.goweii.wanandroid.module.main.presenter.WebPresenter;
import per.goweii.wanandroid.utils.GuideSPUtils;
import per.goweii.wanandroid.utils.RealmHelper;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.WebHolder;
import per.goweii.wanandroid.utils.router.Router;
import per.goweii.wanandroid.widget.CollectView;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebActivity extends BaseActivity<WebPresenter> implements per.goweii.wanandroid.module.main.view.WebView {

    private static final int REQ_CODE_PERMISSION = 1;

    @BindView(R.id.ab)
    ActionBarEx ab;
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.cv_collect)
    CollectView cv_collect;
    @BindView(R.id.iv_into)
    ImageView iv_into;
    @BindView(R.id.wc)
    WebContainer wc;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_forward)
    ImageView iv_forward;
    @BindView(R.id.iv_menu)
    ImageView iv_menu;

    private RuntimeRequester mRuntimeRequester = null;

    private int mArticleId = -1;
    private String mTitle = "";
    private String mAuthor = "";
    private String mUrl = "";

    private RealmHelper mRealmHelper = null;
    private WebGuideDialog mWebGuideDialog = null;
    private WebHolder mWebHolder;

    private List<CollectArticleEntity> mCollectedList = new ArrayList<>(1);
    private WebQuickDialog mWebQuickDialog;

    public static void start(Context context, ArticleBean article) {
        int articleId = article.getOriginId() != 0 ? article.getOriginId() : article.getId();
        start(context, articleId, article.getTitle(), article.getLink(), article.isCollect());
    }

    public static void start(Context context, int articleId, String title, String url, boolean collected) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("articleId", articleId);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("collected", collected);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, String author, String url, boolean collected) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("author", author);
        intent.putExtra("url", url);
        intent.putExtra("collected", collected);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, String url) {
        start(context, title, url, false);
    }

    public static void start(Context context, String title, String url, boolean collected) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("collected", collected);
        context.startActivity(intent);
    }

    public static void start(Context context, String url) {
        start(context, url, false);
    }

    public static void start(Context context, String url, boolean collected) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("collected", collected);
        context.startActivity(intent);
    }

    public void refreshSwipeBackOnlyEdge() {
        mSwipeBackHelper.setSwipeBackOnlyEdge(swipeBackOnlyEdge());
    }

    @Override
    protected boolean swipeBackOnlyEdge() {
        return SettingUtils.getInstance().isWebSwipeBackEdge();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Nullable
    @Override
    protected WebPresenter initPresenter() {
        return new WebPresenter();
    }

    @Override
    protected void initView() {
        Uri uri = Router.uri(getIntent());
        if (uri != null) {
            mUrl = uri.toString();
        } else {
            mArticleId = getIntent().getIntExtra("articleId", -1);
            mTitle = getIntent().getStringExtra("title");
            mAuthor = getIntent().getStringExtra("author");
            mUrl = getIntent().getStringExtra("url");
        }
        mTitle = mTitle == null ? "" : mTitle;
        mAuthor = mAuthor == null ? "" : mAuthor;
        mUrl = mUrl == null ? "" : mUrl;
        boolean collected = getIntent().getBooleanExtra("collected", false);
        if (collected) {
            CollectArticleEntity entity = new CollectArticleEntity();
            entity.setArticleId(mArticleId);
            entity.setTitle(mTitle);
            entity.setAuthor(mAuthor);
            entity.setUrl(mUrl);
            entity.setCollect(true);
            mCollectedList.add(entity);
        }
        iv_menu.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                showMenuDialog();
            }
        });
        iv_back.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                if (mWebHolder.canGoBack()) {
                    mWebHolder.goBack();
                } else {
                    finish();
                }
            }
        });
        iv_forward.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                if (mWebHolder.canGoForward()) {
                    mWebHolder.goForward();
                }
            }
        });
        wc.setOnTouchDownListener(new WebContainer.OnTouchDownListener() {
            @Override
            public void onTouchDown() {
                et_title.clearFocus();
            }
        });
        wc.setOnDoubleClickListener(new WebContainer.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(float x, float y) {
                collect();
            }
        });
        iv_into.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = et_title.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    if (TextUtils.equals(uri.getScheme(), "http") || TextUtils.equals(uri.getScheme(), "https")) {
                        mWebHolder.loadUrl(url);
                    }
                }
                et_title.clearFocus();
            }
        });
        et_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    iv_into.performClick();
                    return true;
                }
                return false;
            }
        });
        et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_title.setText(mWebHolder.getUrl());
                    InputMethodUtils.show(et_title);
                    showQuickDialog();
                } else {
                    setTitle();
                    InputMethodUtils.hide(et_title);
                    dismissQuickDialog();
                }
            }
        });
        cv_collect.setOnClickListener(new CollectView.OnClickListener() {
            @Override
            public void onClick(CollectView v) {
                if (!v.isChecked()) {
                    collect();
                } else {
                    uncollect();
                }
            }
        });

        mRealmHelper = RealmHelper.create();
    }

    private void showQuickDialog() {
        if (mWebQuickDialog == null) {
            mWebQuickDialog = new WebQuickDialog(ab, new WebQuickDialog.OnQuickClickListener() {
                @Override
                public void onCopyLink() {
                    CopyUtils.copyText(mWebHolder.getUrl());
                    ToastMaker.showShort("已复制");
                }

                @Override
                public void onBrowser() {
                    IntentUtils.openBrowser(getContext(), mUrl);
                }

                @Override
                public void onWanPwd() {
                    String url = URLEncoder.encode(mWebHolder.getUrl());
                    StringBuilder s = new StringBuilder();
                    s.append("【玩口令】你的好友给你分享了一个链接，户制泽条消息");
                    s.append(String.format(BuildConfig.WANPWD_FORMAT, BuildConfig.WANPWD_TYPE_WEB, url));
                    s.append("打開最美玩安卓客户端即可查看该网页或者文章");
                    LogUtils.d("UserPageActivity", s);
                    CopyUtils.copyText(s.toString());
                    ToastMaker.showShort("口令已复制");
                }
            });
            mWebQuickDialog.onDismissListener(new Layer.OnDismissListener() {
                @Override
                public void onDismissing(Layer layer) {
                }

                @Override
                public void onDismissed(Layer layer) {
                    et_title.clearFocus();
                }
            });
        }
        mWebQuickDialog.show();
    }

    private void dismissQuickDialog() {
        if (mWebQuickDialog != null) {
            mWebQuickDialog.dismiss();
        }
    }

    private void showMenuDialog() {
        WebMenuDialog.show(getContext(), isCollect(), new WebMenuDialog.OnMenuClickListener() {
            @Override
            public void onShareArticle() {
                ShareArticleActivity.start(getContext(), mWebHolder.getTitle(), mWebHolder.getUrl());
            }

            @Override
            public void onCollect() {
                toggleCollect();
            }

            @Override
            public void onReadLater() {
                if (mRealmHelper != null) {
                    mRealmHelper.add(mWebHolder.getTitle(), mWebHolder.getUrl());
                    ToastMaker.showShort("已加入稍后阅读");
                }
            }

            @Override
            public void onHome() {
                int step = 0;
                while (true) {
                    if (mWebHolder.canGoBackOrForward(step - 1)) {
                        step--;
                    } else {
                        break;
                    }
                }
                mWebHolder.goBackOrForward(step);
            }

            @Override
            public void onRefresh() {
                mWebHolder.reload();
            }

            @Override
            public void onCloseActivity() {
                finish();
            }

            @Override
            public void onShare() {
                new QrcodeShareDialog(getContext(), mWebHolder.getUrl(), mWebHolder.getTitle(), new QrcodeShareDialog.OnShareClickListener() {
                    @Override
                    public void onSave(Bitmap bitmap) {
                        saveQrcodeGallery(bitmap);
                    }

                    @Override
                    public void onShare(Bitmap bitmap) {
                        shareBitmap(bitmap);
                    }
                }).show();
            }
        });
    }

    private void shareBitmap(final Bitmap bitmap) {
        ShareUtils.shareBitmap(getContext(), bitmap);
    }

    private void saveQrcodeGallery(final Bitmap bitmap) {
        mRuntimeRequester = PermissionUtils.request(new RequestListener() {
            @Override
            public void onSuccess() {
                presenter.saveGallery(bitmap, "wanandroid_article_qrcode_" + MD5Coder.encode(mWebHolder.getUrl()) + "_" + System.currentTimeMillis());
            }

            @Override
            public void onFailed() {
            }
        }, getContext(), REQ_CODE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mRuntimeRequester != null) {
            mRuntimeRequester.onActivityResult(requestCode);
        }
    }

    private void setTitle() {
        et_title.setTag(mWebHolder.getUrl());
        if (!TextUtils.isEmpty(mWebHolder.getTitle())) {
            et_title.setText(mWebHolder.getTitle());
        } else {
            et_title.setText(mWebHolder.getUrl());
        }
    }

    private void setCollect() {
        String url = mWebHolder.getUrl();
        boolean contains = false;
        for (CollectArticleEntity entity : mCollectedList) {
            if (TextUtils.equals(entity.getUrl(), url)) {
                contains = true;
                break;
            }
        }
        cv_collect.setChecked(contains, true);
    }

    @Override
    protected void loadData() {
        mWebHolder = WebHolder.with(this, wc)
                .setOnPageTitleCallback(new WebHolder.OnPageTitleCallback() {
                    @Override
                    public void onReceivedTitle(@NonNull String title) {
                        setTitle();
                    }
                })
                .setOnPageLoadCallback(new WebHolder.OnPageLoadCallback() {
                    @Override
                    public void onPageStarted() {
                        et_title.clearFocus();
                    }

                    @Override
                    public void onPageFinished() {
                        if (!GuideSPUtils.getInstance().isWebGuideShown()) {
                            if (mWebGuideDialog == null) {
                                mWebGuideDialog = new WebGuideDialog(getContext());
                                mWebGuideDialog.show();
                            }
                        }
                    }
                })
                .setOnHistoryUpdateCallback(new WebHolder.OnHistoryUpdateCallback() {
                    @Override
                    public void onHistoryUpdate(boolean isReload) {
                        setCollect();
                        if (mWebHolder.canGoBack()) {
                            iv_back.setImageResource(R.drawable.ic_back);
                        } else {
                            iv_back.setImageResource(R.drawable.ic_close);
                        }
                        switchIconEnable(iv_forward, mWebHolder.canGoForward());
                    }
                })
                .setOnPageProgressCallback(new WebHolder.OnPageProgressCallback() {
                    @Override
                    public void onShowProgress() {
                    }

                    @Override
                    public void onProgressChanged(int progress) {
                    }

                    @Override
                    public void onHideProgress() {
                    }
                });
        mWebHolder.loadUrl(mUrl);
    }

    private void switchIconEnable(View view, boolean enable) {
        if (enable) {
            view.setEnabled(true);
            view.setAlpha(1.0F);
        } else {
            view.setEnabled(false);
            view.setAlpha(0.382F);
        }
    }

    @Override
    protected void onPause() {
        mWebHolder.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mWebHolder.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mWebHolder.onDestroy();
        if (mRealmHelper != null) {
            mRealmHelper.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebHolder.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isCollect() {
        String url = mWebHolder.getUrl();
        boolean contains = false;
        for (CollectArticleEntity entity : mCollectedList) {
            if (TextUtils.equals(entity.getUrl(), url)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private void toggleCollect() {
        if (isCollect()) {
            uncollect();
        } else {
            collect();
        }
    }

    private void collect() {
        String url = mWebHolder.getUrl();
        boolean contains = false;
        for (CollectArticleEntity entity : mCollectedList) {
            if (TextUtils.equals(entity.getUrl(), url)) {
                contains = true;
                break;
            }
        }
        if (contains) {
            setCollect();
            return;
        }
        CollectArticleEntity entity = new CollectArticleEntity();
        entity.setCollect(false);
        entity.setUrl(url);
        if (TextUtils.equals(url, mUrl)) {
            if (mArticleId > 0) {
                entity.setArticleId(mArticleId);
            } else {
                entity.setAuthor(mAuthor);
                entity.setTitle(TextUtils.isEmpty(mTitle) ? mWebHolder.getTitle() : mTitle);
            }
        } else {
            entity.setTitle(mWebHolder.getTitle());
        }
        presenter.collect(entity);
    }

    private void uncollect() {
        String url = mWebHolder.getUrl();
        CollectArticleEntity collectArticleEntity = null;
        for (CollectArticleEntity entity : mCollectedList) {
            if (TextUtils.equals(entity.getUrl(), url)) {
                collectArticleEntity = entity;
                break;
            }
        }
        if (collectArticleEntity == null) {
            setCollect();
            return;
        }
        presenter.uncollect(collectArticleEntity);
    }

    @Override
    public void collectSuccess(CollectArticleEntity entity) {
        mCollectedList.add(entity);
        setCollect();
    }

    @Override
    public void collectFailed(String msg) {
        ToastMaker.showShort(msg);
        setCollect();
    }

    @Override
    public void uncollectSuccess(CollectArticleEntity entity) {
        mCollectedList.remove(entity);
        setCollect();
    }

    @Override
    public void uncollectFailed(String msg) {
        ToastMaker.showShort(msg);
        setCollect();
    }
}
