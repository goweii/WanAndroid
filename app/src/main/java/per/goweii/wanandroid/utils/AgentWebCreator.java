package per.goweii.wanandroid.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AgentWebCreator {

    public static AgentWeb create(Activity activity,
                                  FrameLayout container,
                                  String url,
                                  final ClientCallback clientCallback) {
        AgentWeb agentWeb = AgentWeb.with(activity)
                .setAgentWebParent(container, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(ResUtils.getColor(R.color.accent), 1)
                .interceptUnkownUrl()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.layout_agent_web_error, R.id.iv_404)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        if (clientCallback != null) {
                            clientCallback.onReceivedTitle(title);
                        }
                    }
                })
                .setWebViewClient(new WebViewClient() {
                    private boolean shouldInterceptRequest(Uri uri) {
                        LogUtils.d("WebActivity", "interceptUrlRequest:" + uri.toString());
                        switch (SettingUtils.getInstance().getUrlInterceptType()) {
                            default:
                            case WebUrlInterceptUtils.TYPE_NOTHING:
                                return false;
                            case WebUrlInterceptUtils.TYPE_ONLY_WHITE:
                                return !WebUrlInterceptUtils.isWhiteHost(uri.getHost());
                            case WebUrlInterceptUtils.TYPE_INTERCEPT_BLACK:
                                return WebUrlInterceptUtils.isBlackHost(uri.getHost());
                        }
                    }

                    private boolean shouldOverrideUrlLoading(Uri uri) {
                        LogUtils.d("WebActivity", "overrideUrlLoading:" + uri.toString());
                        return false;
                    }

                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                        if (shouldInterceptRequest(Uri.parse(url))) {
                            return new WebResourceResponse(null, null, null);
                        }
                        return super.shouldInterceptRequest(view, url);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        if (shouldInterceptRequest(request.getUrl())) {
                            return new WebResourceResponse(null, null, null);
                        }
                        return super.shouldInterceptRequest(view, request);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (shouldOverrideUrlLoading(Uri.parse(url))) {
                            view.loadUrl(url);
                        }
                        return true;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        if (shouldOverrideUrlLoading(request.getUrl())) {
                            view.loadUrl(request.getUrl().toString());
                        }
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        if (clientCallback != null) {
                            clientCallback.onReceivedUrl(url);
                            clientCallback.onReceivedTitle("");
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        String title = view.getTitle();
                        if (clientCallback != null) {
                            clientCallback.onReceivedTitle(title == null ? "" : title);
                            clientCallback.onPageFinished();
                        }
                    }
                })
                .createAgentWeb()
                .ready()
                .go(url);
        agentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        agentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        agentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(false);
        agentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        agentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        agentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        agentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        return agentWeb;
    }

    public static AgentWeb create(Activity activity, FrameLayout container, String url) {
        return create(activity, container, url, null);
    }

    public interface ClientCallback {
        void onReceivedUrl(String url);

        void onReceivedTitle(String title);

        void onPageFinished();
    }

}
