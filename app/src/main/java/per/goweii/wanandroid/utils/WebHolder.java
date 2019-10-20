package per.goweii.wanandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/10/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebHolder {
    private final AgentWeb agentWeb;

    private OnPageTitleCallback mOnPageTitleCallback = null;
    private OnPageLoadCallback mOnPageLoadCallback = null;
    private OnPageProgressCallback mOnPageProgressCallback = null;
    private OnHistoryUpdateCallback mOnHistoryUpdateCallback = null;

    private boolean isProgressShown = false;

    public static WebHolder with(Activity activity, WebContainer container) {
        return new WebHolder(activity, container);
    }

    private static WebView inflateWebView(Context context) {
        return (WebView) LayoutInflater.from(context).inflate(R.layout.layout_web_view, null);
    }

    private static void syncCookiesForWanAndroid(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String host = Uri.parse(url).getHost();
        if (!TextUtils.equals(host, "www.wanandroid.com")) {
            return;
        }
        List<Cookie> cookies = WanApp.getCookieJar().loadForRequest(HttpUrl.get(url));
        if (cookies == null || cookies.isEmpty()) {
            return;
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookie();
            cookieManager.removeExpiredCookie();
        } else {
            cookieManager.removeSessionCookies(null);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            for (Cookie cookie : cookies) {
                cookieManager.setCookie(url, cookie.name() + "=" + cookie.value());
            }
            CookieSyncManager.createInstance(WanApp.getAppContext());
            CookieSyncManager.getInstance().sync();
        } else {
            for (Cookie cookie : cookies) {
                cookieManager.setCookie(url, cookie.name() + "=" + cookie.value());
            }
            cookieManager.flush();
        }
    }

    private WebHolder(Activity activity, WebContainer container) {
        agentWeb = AgentWeb.with(activity)
                .setAgentWebParent(container, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(ResUtils.getColor(activity, R.color.assist), 1)
                .interceptUnkownUrl()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.layout_agent_web_error, R.id.iv_404)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .setWebChromeClient(new AgentWebChromeClient())
                .setWebViewClient(new AgentWebViewClient())
                .setWebView(inflateWebView(activity))
                .createAgentWeb()
                .ready()
                .go(null);
        getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        getWebView().getSettings().setJavaScriptEnabled(false);
        getWebView().getSettings().setLoadsImagesAutomatically(true);
        getWebView().getSettings().setUseWideViewPort(true);
        getWebView().getSettings().setLoadWithOverviewMode(true);
        getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(getWebView(), true);
        }
    }

    public WebView getWebView() {
        return agentWeb.getWebCreator().getWebView();
    }

    public WebHolder loadUrl(String url) {
        getWebView().loadUrl(url);
        return this;
    }

    @NonNull
    public String getUrl() {
        String url = getWebView().getUrl();
        return url == null ? "" : url;
    }

    @NonNull
    public String getTitle() {
        String title = getWebView().getTitle();
        return title == null ? "" : title;
    }

    public boolean canGoBack() {
        return getWebView().canGoBack();
    }

    public boolean canGoForward() {
        return getWebView().canGoForward();
    }

    public boolean canGoBackOrForward(int steps) {
        return getWebView().canGoBackOrForward(steps);
    }

    public void goBack() {
        getWebView().goBack();
    }

    public void goForward() {
        getWebView().goForward();
    }

    public void goBackOrForward(int steps) {
        getWebView().goBackOrForward(steps);
    }

    public void reload() {
        getWebView().reload();
    }

    public void stopLoading() {
        getWebView().stopLoading();
    }

    public void onPause() {
        agentWeb.getWebLifeCycle().onPause();
    }

    public void onResume() {
        agentWeb.getWebLifeCycle().onResume();
    }

    public void onDestroy() {
        agentWeb.getWebLifeCycle().onDestroy();
    }

    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {
        return agentWeb.handleKeyEvent(keyCode, keyEvent);
    }

    public WebHolder setOnPageTitleCallback(OnPageTitleCallback onPageTitleCallback) {
        mOnPageTitleCallback = onPageTitleCallback;
        return this;
    }

    public WebHolder setOnPageLoadCallback(OnPageLoadCallback onPageLoadCallback) {
        mOnPageLoadCallback = onPageLoadCallback;
        return this;
    }

    public WebHolder setOnPageProgressCallback(OnPageProgressCallback onPageProgressCallback) {
        mOnPageProgressCallback = onPageProgressCallback;
        return this;
    }

    public WebHolder setOnHistoryUpdateCallback(OnHistoryUpdateCallback onHistoryUpdateCallback) {
        mOnHistoryUpdateCallback = onHistoryUpdateCallback;
        return this;
    }

    public class AgentWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mOnPageTitleCallback != null) {
                mOnPageTitleCallback.onReceivedTitle(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress < 95) {
                if (!isProgressShown) {
                    isProgressShown = true;
                    if (mOnPageProgressCallback != null) {
                        mOnPageProgressCallback.onShowProgress();
                    }
                }
                if (mOnPageProgressCallback != null) {
                    mOnPageProgressCallback.onProgressChanged(newProgress);
                }
            } else {
                if (mOnPageProgressCallback != null) {
                    mOnPageProgressCallback.onProgressChanged(newProgress);
                }
                if (isProgressShown) {
                    isProgressShown = false;
                    if (mOnPageProgressCallback != null) {
                        mOnPageProgressCallback.onHideProgress();
                    }
                }
            }
        }
    }

    public class AgentWebViewClient extends WebViewClient {

        private boolean shouldInterceptRequest(Uri uri) {
            syncCookiesForWanAndroid(uri.toString());
            return false;
        }

        private boolean shouldOverrideUrlLoading(Uri uri) {
            switch (SettingUtils.getInstance().getUrlInterceptType()) {
                default:
                case HostInterceptUtils.TYPE_NOTHING:
                    return false;
                case HostInterceptUtils.TYPE_ONLY_WHITE:
                    return !HostInterceptUtils.isWhiteHost(uri.getHost());
                case HostInterceptUtils.TYPE_INTERCEPT_BLACK:
                    return HostInterceptUtils.isBlackHost(uri.getHost());
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
            if (mOnPageTitleCallback != null) {
                mOnPageTitleCallback.onReceivedTitle("");
            }
            if (mOnPageLoadCallback != null) {
                mOnPageLoadCallback.onPageStarted();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mOnPageTitleCallback != null) {
                mOnPageTitleCallback.onReceivedTitle(getTitle());
            }
            if (mOnPageLoadCallback != null) {
                mOnPageLoadCallback.onPageFinished();
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (mOnHistoryUpdateCallback != null) {
                mOnHistoryUpdateCallback.onHistoryUpdate(isReload);
            }
        }
    }

    public interface OnPageTitleCallback {
        void onReceivedTitle(@NonNull String title);
    }

    public interface OnPageLoadCallback {
        void onPageStarted();

        void onPageFinished();
    }

    public interface OnPageProgressCallback {
        void onShowProgress();

        void onProgressChanged(int progress);

        void onHideProgress();
    }

    public interface OnHistoryUpdateCallback {
        void onHistoryUpdate(boolean isReload);
    }
}
