package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.listener.SimpleTextWatcher;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.presenter.ShareArticlePresenter;
import per.goweii.wanandroid.module.main.view.ShareArticleView;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.WebHolder;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ShareArticleActivity extends BaseActivity<ShareArticlePresenter> implements ShareArticleView {

    private static final String TAG = ShareArticleActivity.class.getSimpleName();

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.wc)
    WebContainer wc;
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_link)
    EditText et_link;

    private WebHolder mWebHolder;

    public static void start(Context context) {
        start(context, "");
    }

    public static void start(Context context, String link) {
        start(context, "", link);
    }

    public static void start(Context context, String title, String link) {
        if (!UserUtils.getInstance().doIfLogin(context)) {
            return;
        }
        Intent intent = new Intent(context, ShareArticleActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_article;
    }

    @Nullable
    @Override
    protected ShareArticlePresenter initPresenter() {
        return new ShareArticlePresenter();
    }

    @Override
    protected void initView() {
        abc.setOnRightTextClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    et_title.requestFocus();
                    return;
                }
                String link = et_link.getText().toString();
                if (TextUtils.isEmpty(link)) {
                    et_link.requestFocus();
                    return;
                }
                InputMethodUtils.hide(et_link);
                presenter.shareArticle(title, link);
            }
        });
        et_link.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                LogUtils.i(TAG, "afterTextChanged=" + s.toString());
                refreshTitle(s.toString());
            }
        });
    }

    @Override
    protected void loadData() {
        String title = getIntent().getStringExtra("title");
        String link = getIntent().getStringExtra("link");
        if (!TextUtils.isEmpty(title)) {
            et_title.setText(title);
        }
        if (!TextUtils.isEmpty(link)) {
            et_link.setText(link);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebHolder != null) {
            mWebHolder.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebHolder != null) {
            mWebHolder.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebHolder != null) {
            mWebHolder.onDestroy();
        }
    }

    private void refreshTitle(String url) {
        LogUtils.i(TAG, "refreshTitle=" + url);
        if (TextUtils.isEmpty(url)) {
            et_title.setText("");
            return;
        }
        if (mWebHolder == null) {
            mWebHolder = WebHolder.with(this, wc)
                    .setOnPageTitleCallback(new WebHolder.OnPageTitleCallback() {
                        @Override
                        public void onReceivedTitle(@NonNull String title) {
                            et_title.setText(title);
                            et_title.setSelection(title.length());
                        }
                    })
                    .loadUrl(url);
        } else {
            mWebHolder.stopLoading();
            mWebHolder.loadUrl(url);
        }
    }

    @OnClick({R.id.tv_open, R.id.tv_refresh})
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onClick2(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_refresh:
                refreshTitle(et_link.getText().toString());
                break;
            case R.id.tv_open:
                WebActivity.start(getContext(), et_link.getText().toString());
                break;
        }
    }

    @Override
    public void shareArticleSuccess(int code, BaseBean data) {
        finish();
    }

    @Override
    public void shareArticleFailed(int code, String msg) {
        ToastMaker.showShort(msg);
    }
}
