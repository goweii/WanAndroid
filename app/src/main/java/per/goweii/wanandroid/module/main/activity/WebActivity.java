package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.IntentUtils;
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

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.wc)
    WebContainer wc;

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
        mAuthor = getIntent().getStringExtra("author");
        mUrl = getIntent().getStringExtra("url");
        mCurrUrl = mUrl;
        mCurrTitle = mTitle;

        // forceHttpsForAndroid9();

        abc.getTitleTextView().setText(mTitle);
        abc.setOnLeftIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAgentWeb.back()) {
                    finish();
                }
            }
        });
        abc.setOnRightIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                WebMenuDialog.show(abc, new WebMenuDialog.OnMenuClickListener() {
                    @Override
                    public void onCollect() {
                        collect();
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
                });
            }
        });

        wc.setOnDoubleClickListener(new WebContainer.OnDoubleClickListener() {
            @Override
            public void onDoubleClick() {
                collect();
            }
        });

        mRealmHelper = RealmHelper.create();
    }

    @Override
    protected void loadData() {
        mAgentWeb = AgentWebCreator.create(this, wc, mUrl, new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mCurrTitle = title;
                mCurrUrl = view.getUrl();
                if (abc.getTitleTextView() != null) {
                    abc.getTitleTextView().setText(title);
                }
            }
        }, new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!GuideSPUtils.getInstance().isWebGuideShown()) {
                    if (mWebGuideDialog == null) {
                        mWebGuideDialog = new WebGuideDialog(abc);
                        mWebGuideDialog.show();
                    }
                }
            }
        });
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

    private void collect() {
        if (TextUtils.equals(mCurrUrl, mUrl)) {
            if (mArticleId != -1) {
                presenter.collect(mArticleId);
            } else {
                if (TextUtils.isEmpty(mAuthor)) {
                    presenter.collect(mTitle, mUrl);
                } else {
                    presenter.collect(mTitle, mAuthor, mUrl);
                }
            }
        } else {
            presenter.collect(mCurrTitle, mCurrUrl);
        }
    }

    private void forceHttpsForAndroid9() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        if (mUrl == null) {
            return;
        }
        if (mUrl.startsWith("http://")) {
            mUrl = mUrl.replace("http://", "https://");
        }
    }

    @Override
    public void collectSuccess() {
        ToastMaker.showShort("收藏成功");
    }

    @Override
    public void collectFailed(String msg) {
        ToastMaker.showShort(msg);
    }
}
