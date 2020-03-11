package per.goweii.wanandroid.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebSettings;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import per.goweii.basic.utils.LogUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.widget.WebContainer;
import per.goweii.wanandroid.widget.X5WebView;

/**
 * @author CuiZhen
 * @date 2019/10/20
 * GitHub: https://github.com/goweii
 */
public class WebHolder {
    private OnPageTitleCallback mOnPageTitleCallback = null;
    private OnPageLoadCallback mOnPageLoadCallback = null;
    private OnPageProgressCallback mOnPageProgressCallback = null;
    private OnHistoryUpdateCallback mOnHistoryUpdateCallback = null;

    private boolean isProgressShown = false;
    private WebView mWebView;
    private final MaterialProgressBar mProgressBar;

    public static WebHolder with(Activity activity, WebContainer container) {
        return new WebHolder(activity, container);
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

    @SuppressLint("SetJavaScriptEnabled")
    private WebHolder(Activity activity, WebContainer container) {
        activity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mWebView = new X5WebView(activity);
        mProgressBar = (MaterialProgressBar) LayoutInflater.from(activity).inflate(R.layout.basic_ui_progress_bar, container, false);
        mProgressBar.setMax(100);
        container.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        container.addView(mProgressBar, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                activity.getResources().getDimensionPixelSize(R.dimen.basic_ui_action_bar_loading_bar_height)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }
        mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mWebView.getView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebView.setWebChromeClient(new WanWebChromeClient());
        mWebView.setWebViewClient(new WanWebViewClient());
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(false);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        IX5WebSettingsExtension ext = mWebView.getSettingsExtension();
        if (ext != null) {
            ext.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
            if (NightModeUtils.isNightMode(activity)) {
                container.setDarkMaskEnable(false);
                ext.setDayOrNight(false);
            } else {
                ext.setDayOrNight(true);
            }
        }
    }

    public WebHolder loadUrl(String url) {
        mWebView.loadUrl(url);
        return this;
    }

    @NonNull
    public String getUrl() {
        String url = mWebView.getUrl();
        return url == null ? "" : url;
    }

    @NonNull
    public String getTitle() {
        String title = mWebView.getTitle();
        return title == null ? "" : title;
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public boolean canGoForward() {
        return mWebView.canGoForward();
    }

    public boolean canGoBackOrForward(int steps) {
        return mWebView.canGoBackOrForward(steps);
    }

    public void goBack() {
        mWebView.goBack();
    }

    public void goForward() {
        mWebView.goForward();
    }

    public void goBackOrForward(int steps) {
        mWebView.goBackOrForward(steps);
    }

    public void reload() {
        mWebView.reload();
    }

    public void stopLoading() {
        mWebView.stopLoading();
    }

    public void onPause() {
        mWebView.onPause();
    }

    public void onResume() {
        mWebView.onResume();
    }

    public void onDestroy() {
        ViewParent parent = mWebView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mWebView);
        }
        mWebView.removeAllViews();
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        mWebView.stopLoading();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.destroy();
    }

    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
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

    public class WanWebChromeClient extends WebChromeClient {

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
                    onShowProgress();
                }
                onProgressChanged(newProgress);
            } else {
                onProgressChanged(newProgress);
                if (isProgressShown) {
                    isProgressShown = false;
                    onHideProgress();
                }
            }
        }

        private void onShowProgress() {
            mProgressBar.setVisibility(View.VISIBLE);
            setProgress(0);
            if (mOnPageProgressCallback != null) {
                mOnPageProgressCallback.onShowProgress();
            }
        }

        private void onProgressChanged(int progress) {
            setProgress(progress);
            if (mOnPageProgressCallback != null) {
                mOnPageProgressCallback.onProgressChanged(progress);
            }
        }

        private void onHideProgress() {
            mProgressBar.setVisibility(View.GONE);
            setProgress(100);
            if (mOnPageProgressCallback != null) {
                mOnPageProgressCallback.onHideProgress();
            }
        }

        private void setProgress(int progress) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mProgressBar.setProgress(progress, true);
            } else {
                mProgressBar.setProgress(progress);
            }
        }
    }

    public class WanWebViewClient extends WebViewClient {

        private boolean shouldInterceptRequest(Uri uri) {
            syncCookiesForWanAndroid(uri.toString());
            return false;
        }

        private boolean shouldOverrideUrlLoading(Uri uri) {
            LogUtils.d("WebView", "shouldOverrideUrlLoading->" + uri.toString());
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
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request, Bundle bundle) {
            if (shouldInterceptRequest(request.getUrl())) {
                return new WebResourceResponse(null, null, null);
            }
            return super.shouldInterceptRequest(webView, request, bundle);
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
                mOnPageTitleCallback.onReceivedTitle(getUrl());
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
