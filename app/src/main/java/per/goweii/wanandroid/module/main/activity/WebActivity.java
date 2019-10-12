package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.just.agentweb.AgentWeb;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.IntentUtils;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.dialog.WebGuideDialog;
import per.goweii.wanandroid.module.main.dialog.WebMenuDialog;
import per.goweii.wanandroid.module.main.presenter.WebPresenter;
import per.goweii.wanandroid.utils.AgentWebCreator;
import per.goweii.wanandroid.utils.GuideSPUtils;
import per.goweii.wanandroid.utils.RealmHelper;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebActivity extends BaseActivity<WebPresenter> implements per.goweii.wanandroid.module.main.view.WebView {

    @BindView(R.id.abs)
    ActionBarSearch abs;
    @BindView(R.id.wc)
    WebContainer wc;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_forward)
    ImageView iv_forward;
    @BindView(R.id.iv_menu)
    ImageView iv_menu;
    @BindView(R.id.iv_refresh)
    ImageView iv_refresh;
    @BindView(R.id.iv_home)
    ImageView iv_home;

    private int mArticleId = -1;
    private String mTitle = "";
    private String mAuthor = "";
    private String mUrl = "";

    private String mCurrTitle = "";
    private String mCurrUrl = "";

    private AgentWeb mAgentWeb = null;
    private RealmHelper mRealmHelper = null;
    private WebGuideDialog mWebGuideDialog = null;

    public static void start(Context context, int articleId, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("articleId", articleId);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, String author, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("author", author);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
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
        mArticleId = getIntent().getIntExtra("articleId", -1);
        mTitle = getIntent().getStringExtra("title");
        mTitle = mTitle == null ? "" : mTitle;
        mAuthor = getIntent().getStringExtra("author");
        mAuthor = mAuthor == null ? "" : mAuthor;
        mUrl = getIntent().getStringExtra("url");
        mUrl = mUrl == null ? "" : mUrl;
        mCurrUrl = mUrl;
        mCurrTitle = mTitle;

        setTitle();
        iv_menu.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                LogUtils.i("WebActivity", "mArticleId=" + mArticleId);
                LogUtils.i("WebActivity", "mTitle=" + mTitle);
                LogUtils.i("WebActivity", "mAuthor=" + mAuthor);
                LogUtils.i("WebActivity", "mUrl=" + mUrl);
                LogUtils.i("WebActivity", "mCurrUrl=" + mCurrUrl);
                LogUtils.i("WebActivity", "mCurrTitle=" + mCurrTitle);
                WebMenuDialog.show(getContext(), new WebMenuDialog.OnMenuClickListener() {
                    @Override
                    public void onShare() {
                        ShareArticleActivity.start(getContext(), mCurrTitle, mCurrUrl);
                    }

                    @Override
                    public void onCollect() {
                        collect(null);
                    }

                    @Override
                    public void onReadLater() {
                        if (mRealmHelper != null) {
                            mRealmHelper.add(mCurrTitle, mCurrUrl);
                            ToastMaker.showShort("已加入稍后阅读");
                        }
                    }

                    @Override
                    public void onBrowser() {
                        IntentUtils.openBrowser(getContext(), mUrl);
                    }

                    @Override
                    public void onCloseActivity() {
                        finish();
                    }
                });
            }
        });
        iv_back.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                if (mAgentWeb.getWebCreator().getWebView().canGoBack()) {
                    mAgentWeb.getWebCreator().getWebView().goBack();
                }
            }
        });
        iv_forward.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                if (mAgentWeb.getWebCreator().getWebView().canGoForward()) {
                    mAgentWeb.getWebCreator().getWebView().goForward();
                }
            }
        });
        iv_refresh.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                if (iv_refresh.getAnimation() == null) {
                    mAgentWeb.getWebCreator().getWebView().reload();
                } else {
                    mAgentWeb.getWebCreator().getWebView().stopLoading();
                }
            }
        });
        iv_home.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                int step = 0;
                while (true) {
                    if (mAgentWeb.getWebCreator().getWebView().canGoBackOrForward(step - 1)) {
                        step--;
                    } else {
                        break;
                    }
                }
                mAgentWeb.getWebCreator().getWebView().goBackOrForward(step);
            }
        });
        wc.setOnTouchDownListener(new WebContainer.OnTouchDownListener() {
            @Override
            public void onTouchDown() {
                abs.getEditTextView().clearFocus();
            }
        });
        wc.setOnDoubleClickListener(new WebContainer.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(float x, float y) {
                collect(new PointF(x, y));
            }
        });
        abs.setOnRightTextClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                String url = abs.getEditTextView().getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    if (TextUtils.equals(uri.getScheme(), "http") || TextUtils.equals(uri.getScheme(), "https")) {
                        mAgentWeb.getWebCreator().getWebView().loadUrl(url);
                    }
                }
                abs.getEditTextView().clearFocus();
            }
        });
        abs.getEditTextView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    abs.getEditTextView().setText(mCurrUrl);
                    InputMethodUtils.show(abs.getEditTextView());
                } else {
                    setTitle();
                    InputMethodUtils.hide(abs.getEditTextView());
                }
            }
        });

        mRealmHelper = RealmHelper.create();
    }

    private void setTitle() {
        abs.getEditTextView().setTag(mCurrUrl);
        if (!TextUtils.isEmpty(mCurrTitle)) {
            abs.getEditTextView().setText(mCurrTitle);
        } else {
            abs.getEditTextView().setText(mCurrUrl);
        }
    }

    @Override
    protected void loadData() {
        mAgentWeb = AgentWebCreator.create(this, wc, mUrl, new AgentWebCreator.ClientCallback() {
            @Override
            public void onReceivedUrl(String url) {
                mCurrUrl = url;
                mCurrTitle = "";
            }

            @Override
            public void onReceivedTitle(String title) {
                mCurrTitle = title;
                setTitle();
            }

            @Override
            public void onHistoryUpdate(boolean isReload) {
                switchIconEnable(iv_back, mAgentWeb.getWebCreator().getWebView().canGoBack());
                switchIconEnable(iv_forward, mAgentWeb.getWebCreator().getWebView().canGoForward());
                switchIconEnable(iv_home, mAgentWeb.getWebCreator().getWebView().canGoBack());
            }

            @Override
            public void onPageStarted() {
                abs.getEditTextView().clearFocus();
            }

            @Override
            public void onProgressChanged(int progress) {
                if (progress < 95) {
                    if (iv_refresh.getAnimation() == null) {
                        RotateAnimation anim = new RotateAnimation(0, 360,
                                Animation.RELATIVE_TO_SELF, 0.5F,
                                Animation.RELATIVE_TO_SELF, 0.5F);
                        anim.setDuration(1500);
                        anim.setRepeatMode(Animation.RESTART);
                        anim.setRepeatCount(Animation.INFINITE);
                        iv_refresh.startAnimation(anim);
                    }
                } else {
                    if (iv_refresh.getAnimation() != null) {
                        iv_refresh.clearAnimation();
                    }
                }
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
        });
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
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        if (mRealmHelper != null) {
            mRealmHelper.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void collect(PointF p) {
        if (TextUtils.equals(mCurrUrl, mUrl)) {
            if (mArticleId != -1) {
                presenter.collect(mArticleId, p);
            } else {
                if (TextUtils.isEmpty(mAuthor)) {
                    presenter.collect(TextUtils.isEmpty(mTitle) ? mCurrTitle : mTitle, mUrl, p);
                } else {
                    presenter.collect(TextUtils.isEmpty(mTitle) ? mCurrTitle : mTitle, mAuthor, mUrl, p);
                }
            }
        } else {
            presenter.collect(mCurrTitle, mCurrUrl, p);
        }
    }

    @Override
    public void collectSuccess(PointF p) {
        if (p == null) {
            wc.showCollectAnim();
        } else {
            wc.showCollectAnim(p.x, p.y);
        }
    }

    @Override
    public void collectFailed(String msg) {
        ToastMaker.showShort(msg);
    }
}
