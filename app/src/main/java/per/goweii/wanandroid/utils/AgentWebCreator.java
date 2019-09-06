package per.goweii.wanandroid.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

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
                .setWebChromeClient(new AgentWebChromeClient(clientCallback))
                .setWebViewClient(new AgentWebViewClient(clientCallback))
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

    public static class AgentWebChromeClient extends WebChromeClient {
        private final ClientCallback mClientCallback;

        public AgentWebChromeClient() {
            mClientCallback = null;
        }

        public AgentWebChromeClient(ClientCallback clientCallback) {
            mClientCallback = clientCallback;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mClientCallback != null) {
                mClientCallback.onReceivedTitle(title);
            }
        }
    }

    public static class AgentWebViewClient extends WebViewClient {
        private final ClientCallback mClientCallback;

        public AgentWebViewClient() {
            mClientCallback = null;
        }

        public AgentWebViewClient(ClientCallback clientCallback) {
            mClientCallback = clientCallback;
        }

        private boolean shouldInterceptRequest(Uri uri) {
            LogUtils.d("AgentWebCreator", "interceptUrlRequest:" + uri.toString());
            return false;
        }

        /**
         * true     拦截
         * false    加载
         */
        private boolean shouldOverrideUrlLoading(Uri uri) {
            LogUtils.d("AgentWebCreator", "overrideUrlLoading:" + uri.toString());
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
            return shouldOverrideUrlLoading(Uri.parse(url));
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(request.getUrl());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mClientCallback != null) {
                mClientCallback.onReceivedUrl(url);
                mClientCallback.onReceivedTitle("");
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = view.getTitle();
            if (mClientCallback != null) {
                mClientCallback.onReceivedTitle(title == null ? "" : title);
                mClientCallback.onPageFinished();
            }
        }
    }

}
