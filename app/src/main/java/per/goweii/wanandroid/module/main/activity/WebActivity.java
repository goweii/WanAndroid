package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.just.agentweb.AgentWeb;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarSuper;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.toast.ToastMaker;
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
    ActionBarSuper abs;
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

        abs.getTitleTextView().setText(mTitle);
        abs.getLeftActionView(0).setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                if (!mAgentWeb.back()) {
                    finish();
                }
            }
        });
        abs.getRightActionView(0).setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                LogUtils.i("WebActivity", "mArticleId=" + mArticleId);
                LogUtils.i("WebActivity", "mTitle=" + mTitle);
                LogUtils.i("WebActivity", "mAuthor=" + mAuthor);
                LogUtils.i("WebActivity", "mUrl=" + mUrl);
                LogUtils.i("WebActivity", "mCurrUrl=" + mCurrUrl);
                LogUtils.i("WebActivity", "mCurrTitle=" + mCurrTitle);
                WebMenuDialog.show(abs, new WebMenuDialog.OnMenuClickListener() {
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
                });
            }
        });

        wc.setOnDoubleClickListener(new WebContainer.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(float x, float y) {
                collect(new PointF(x, y));
            }
        });

        mRealmHelper = RealmHelper.create();
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
                if (abs.getTitleTextView() != null) {
                    abs.getTitleTextView().setText(mCurrTitle);
                }
            }

            @Override
            public void onPageFinished() {
                if (!GuideSPUtils.getInstance().isWebGuideShown()) {
                    if (mWebGuideDialog == null) {
                        mWebGuideDialog = new WebGuideDialog(abs);
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

    private void collect(PointF p) {
        if (TextUtils.equals(mCurrUrl, mUrl)) {
            if (mArticleId != -1) {
                presenter.collect(mArticleId, p);
            } else {
                if (TextUtils.isEmpty(mAuthor)) {
                    presenter.collect(mTitle, mUrl, p);
                } else {
                    presenter.collect(mTitle, mAuthor, mUrl, p);
                }
            }
        } else {
            presenter.collect(mCurrTitle, mCurrUrl, p);
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
