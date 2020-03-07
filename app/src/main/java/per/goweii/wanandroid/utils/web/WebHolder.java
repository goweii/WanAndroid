package per.goweii.wanandroid.utils.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebSettings;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.utils.NightModeUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.web.js.DarkmodeInject;
import per.goweii.wanandroid.utils.web.js.VConsoleInject;
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
    private OverrideUrlInterceptor mOverrideUrlInterceptor = null;
    private InterceptUrlInterceptor mInterceptUrlInterceptor = null;
    private NightModeInterceptor mNightModeInterceptor = null;

    private boolean isProgressShown = false;
    private WebView mWebView;
    private final MaterialProgressBar mProgressBar;

    private final VConsoleInject vConsoleInject;
    private final DarkmodeInject darkmodeInject;

    private final Activity mAactivity;
    private final WebContainer mWebContainer;
    private final TextView mWebHostWarning;
    private final SmartRefreshLayout mOverScrollLayout;

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
        mAactivity = activity;
        mWebContainer = container;
        mWebView = new X5WebView(activity);
        mWebView.setBackgroundResource(R.color.foreground);
        mOverScrollLayout = new SmartRefreshLayout(activity);
        mOverScrollLayout.setEnablePureScrollMode(true);
        mWebHostWarning = new TextView(activity);
        mWebHostWarning.setAlpha(0.3F);
        mWebHostWarning.setTextColor(activity.getResources().getColor(R.color.text_third));
        mWebHostWarning.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.text_notes));
        int ph = (int) activity.getResources().getDimension(R.dimen.margin_middle);
        int pv = (int) activity.getResources().getDimension(R.dimen.margin_def);
        mWebHostWarning.setPadding(ph, pv, ph, pv);
        mWebHostWarning.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        mOverScrollLayout.addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mProgressBar = (MaterialProgressBar) LayoutInflater.from(activity).inflate(R.layout.basic_ui_progress_bar, container, false);
        mProgressBar.setMax(100);
        container.addView(mWebHostWarning, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        container.addView(mOverScrollLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
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
        //webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        IX5WebSettingsExtension ext = mWebView.getSettingsExtension();
        if (ext != null) {
            ext.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
            boolean isAppDarkMode = NightModeUtils.isNightMode(activity);
            if (isAppDarkMode) {
                container.setDarkMaskEnable(false);
                ext.setDayOrNight(false);
            } else {
                ext.setDayOrNight(true);
            }
        }
        vConsoleInject = new VConsoleInject(mWebView);
        darkmodeInject = new DarkmodeInject(mWebView);
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

    @NonNull
    public String getUserAgent() {
        String userAgentString = mWebView.getSettings().getUserAgentString();
        return userAgentString == null ? "" : userAgentString;
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

    public WebHolder setOverrideUrlInterceptor(OverrideUrlInterceptor overrideUrlInterceptor) {
        mOverrideUrlInterceptor = overrideUrlInterceptor;
        return this;
    }

    public WebHolder setInterceptUrlInterceptor(InterceptUrlInterceptor interceptUrlInterceptor) {
        mInterceptUrlInterceptor = interceptUrlInterceptor;
        return this;
    }

    public WebHolder setNightModeInterceptor(NightModeInterceptor nightModeInterceptor) {
        mNightModeInterceptor = nightModeInterceptor;
        IX5WebSettingsExtension ext = mWebView.getSettingsExtension();
        if (ext != null) {
            boolean isAppDarkMode = NightModeUtils.isNightMode(mAactivity);
            if (isAppDarkMode) {
                boolean shouldNightMode;
                if (mNightModeInterceptor != null) {
                    shouldNightMode = mNightModeInterceptor.shouldNightMode();
                } else {
                    shouldNightMode = true;
                }
                if (shouldNightMode) {
                    mWebContainer.setDarkMaskEnable(false);
                    ext.setDayOrNight(false);
                } else {
                    ext.setDayOrNight(true);
                }
            } else {
                ext.setDayOrNight(true);
            }
        }
        return this;
    }

    public WebHolder setOnOverScrollListener(OnOverScrollListener onOverScrollListener) {
        mOverScrollLayout.setOnMultiPurposeListener(new OnMultiPurposeListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                if (onOverScrollListener != null) {
                    onOverScrollListener.onHeaderMoving(percent);
                }
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {
            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
                if (onOverScrollListener != null) {
                    onOverScrollListener.onFooterMoving(percent);
                }
            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {
            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) {
            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (onOverScrollListener != null) {
                    onOverScrollListener.onFooterConfirm();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (onOverScrollListener != null) {
                    onOverScrollListener.onHeaderConfirm();
                }
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }
        });
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
            vConsoleInject.onProgressChanged(newProgress);
            darkmodeInject.onProgressChanged(newProgress);
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
        private WebResourceResponse shouldInterceptRequest(@NonNull Uri pageUri,
                                                           @NonNull Uri reqUri,
                                                           @Nullable Map<String, String> reqHeaders,
                                                           @Nullable String reqMethod) {
            syncCookiesForWanAndroid(reqUri.toString());
            if (mInterceptUrlInterceptor == null) {
                return null;
            }
            return mInterceptUrlInterceptor.onInterceptUrl(pageUri, reqUri, reqHeaders, reqMethod);
        }

        private boolean shouldOverrideUrlLoading(Uri uri) {
            if (mOverrideUrlInterceptor == null) {
                switch (SettingUtils.getInstance().getUrlInterceptType()) {
                    default:
                    case HostInterceptUtils.TYPE_NOTHING:
                        return false;
                    case HostInterceptUtils.TYPE_ONLY_WHITE:
                        return !HostInterceptUtils.isWhiteHost(uri.getHost());
                    case HostInterceptUtils.TYPE_INTERCEPT_BLACK:
                        return HostInterceptUtils.isBlackHost(uri.getHost());
                }
            } else {
                return mOverrideUrlInterceptor.onOverrideUrl(uri.toString());
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            String pageUrl = view.getUrl();
            if (TextUtils.isEmpty(pageUrl)) {
                return super.shouldInterceptRequest(view, url);
            }
            Uri pageUri = Uri.parse(pageUrl);
            if (TextUtils.isEmpty(url)) {
                return super.shouldInterceptRequest(view, url);
            }
            Uri reqUri = Uri.parse(url);
            WebResourceResponse response = shouldInterceptRequest(pageUri, reqUri, null, null);
            if (response != null) {
                return response;
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String pageUrl = view.getUrl();
            if (TextUtils.isEmpty(pageUrl)) {
                return super.shouldInterceptRequest(view, request);
            }
            Uri pageUri = Uri.parse(pageUrl);
            Uri reqUri = request.getUrl();
            if (reqUri == null) {
                return super.shouldInterceptRequest(view, request);
            }
            Map<String, String> reqHeaders = request.getRequestHeaders();
            String reqMethod = request.getMethod();
            WebResourceResponse response = shouldInterceptRequest(pageUri, reqUri, reqHeaders, reqMethod);
            if (response != null) {
                return response;
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request, Bundle bundle) {
            String pageUrl = view.getUrl();
            if (TextUtils.isEmpty(pageUrl)) {
                return super.shouldInterceptRequest(view, request, bundle);
            }
            Uri pageUri = Uri.parse(pageUrl);
            Uri reqUri = request.getUrl();
            if (reqUri == null) {
                return super.shouldInterceptRequest(view, request, bundle);
            }
            Map<String, String> reqHeaders = request.getRequestHeaders();
            String reqMethod = request.getMethod();
            WebResourceResponse response = shouldInterceptRequest(pageUri, reqUri, reqHeaders, reqMethod);
            if (response != null) {
                return response;
            } else {
                return super.shouldInterceptRequest(view, request, bundle);
            }
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
            vConsoleInject.onPageStarted();
            darkmodeInject.onPageStarted();
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
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String host = uri.getHost();
                if (!TextUtils.isEmpty(host)) {
                    mWebHostWarning.setText(String.format("网页由 %s 提供\nX5内核：" + isX5Enabled(), host));
                }
            }
            if (mOnHistoryUpdateCallback != null) {
                mOnHistoryUpdateCallback.onHistoryUpdate(isReload);
            }
        }
    }

    private Boolean x5Enabled = null;

    private String isX5Enabled() {
        if (x5Enabled == null) {
            x5Enabled = QbSdk.canLoadX5(WanApp.getAppContext());
        }
        if (x5Enabled) {
            return "已启用";
        } else {
            return "未启用";
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

    public interface OverrideUrlInterceptor {
        boolean onOverrideUrl(String url);
    }

    public interface InterceptUrlInterceptor {
        @Nullable
        WebResourceResponse onInterceptUrl(@NonNull Uri pageUri,
                                           @NonNull Uri reqUri,
                                           @Nullable Map<String, String> reqHeaders,
                                           @Nullable String reqMethod);
    }

    public interface NightModeInterceptor {
        boolean shouldNightMode();
    }

    public interface OnOverScrollListener {
        void onHeaderMoving(float percent);

        void onHeaderConfirm();

        void onFooterMoving(float percent);

        void onFooterConfirm();
    }
}
