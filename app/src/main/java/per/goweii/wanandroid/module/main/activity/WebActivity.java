package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarCommon;
import per.goweii.actionbarex.listener.OnLeftIconClickListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebActivity extends BaseActivity {

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.fl)
    FrameLayout fl;
    private AgentWeb mAgentWeb;
    private String mUrl;

    public static void start(Context context, String title, String url){
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        String title = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");

        // forceHttpsForAndroid9();

        abc.getTitleTextView().setText(title);
        abc.setOnLeftImageClickListener(new OnLeftIconClickListener() {
            @Override
            public void onClick() {
                if (!mAgentWeb.back()){
                    finish();
                }
            }
        });
    }

    @Override
    protected void loadData() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(fl, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(ResUtils.getColor(R.color.accent), 1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.layout_agent_web_error, R.id.iv_404)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                // .interceptUnkownUrl()
                .setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        if (abc.getTitleTextView() != null) {
                            abc.getTitleTextView().setText(title);
                        }
                    }
                })
                .setWebViewClient(new WebViewClient())
                .createAgentWeb()
                .ready()
                .go(mUrl);
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
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void forceHttpsForAndroid9(){
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

}
