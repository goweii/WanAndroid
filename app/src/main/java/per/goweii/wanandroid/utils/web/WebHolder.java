package per.goweii.wanandroid.utils.web;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Cookie;
import per.goweii.anylayer.DecorLayer;
import per.goweii.anylayer.ext.NullAnimatorCreator;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.module.main.dialog.ImagePreviewDialog;
import per.goweii.wanandroid.utils.CookieUtils;
import per.goweii.wanandroid.utils.DarkModeUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.web.js.JsInjector;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/10/20
 * GitHub: https://github.com/goweii
 */
public class WebHolder {
    private static final String TAG = WebHolder.class.getSimpleName();

    private OnPageScrollChangeListener mOnPageScrollChangeListener = null;
    private OnPageScrollEndListener mOnPageScrollEndListener = null;
    private OnPageTitleCallback mOnPageTitleCallback = null;
    private OnPageLoadCallback mOnPageLoadCallback = null;
    private OnPageProgressCallback mOnPageProgressCallback = null;
    private OnHistoryUpdateCallback mOnHistoryUpdateCallback = null;
    private OverrideUrlInterceptor mOverrideUrlInterceptor = null;
    private InterceptUrlInterceptor mInterceptUrlInterceptor = null;

    private final Activity mActivity;
    private final WebContainer mWebContainer;
    private final WebView mWebView;
    private final ProgressBar mProgressBar;
    private final String mUserAgentString;

    private boolean allowOpenOtherApp = true;
    private boolean allowOpenDownload = true;
    private boolean allowRedirect = true;

    private final JsInjector jsInjector;

    private boolean useInstanceCache = false;
    private boolean isProgressShown = false;
    private boolean isPageScrollEnd = false;

    public static WebHolder with(Activity activity, WebContainer container, ProgressBar progressBar) {
        return new WebHolder(activity, container, progressBar);
    }

    public static WebHolder with(Activity activity, WebContainer container) {
        return new WebHolder(activity, container, null);
    }

    private static void syncCookiesForWanAndroid(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String host = Uri.parse(url).getHost();
        if (!TextUtils.equals(host, "www.wanandroid.com")) {
            return;
        }
        List<Cookie> cookies = CookieUtils.INSTANCE.loadForUrl(url);
        if (cookies.isEmpty()) {
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
    private WebHolder(Activity activity, WebContainer container, ProgressBar progressBar) {
        activity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mActivity = activity;
        mWebContainer = container;
        int color = ResUtils.getThemeColor(mWebContainer, R.attr.colorSurface);
        mWebContainer.setBackgroundColor(color);
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            QbSdk.forceSysWebView();
        } else {
            QbSdk.unForceSysWebView();
        }
        if (useInstanceCache) {
            mWebView = WebInstance.getInstance(mActivity).obtain();
        } else {
            mWebView = WebInstance.getInstance(mActivity).create();
        }
        container.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        if (progressBar == null) {
            mProgressBar = (MaterialProgressBar) LayoutInflater.from(activity)
                    .inflate(R.layout.basic_ui_progress_bar, container, false);
            container.addView(mProgressBar, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    activity.getResources().getDimensionPixelSize(R.dimen.basic_ui_action_bar_loading_bar_height)));
        } else {
            mProgressBar = progressBar;
        }
        mProgressBar.setMax(100);
        mWebView.setWebChromeClient(new WanWebChromeClient());
        mWebView.setWebViewClient(new WanWebViewClient());
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                LogUtils.i(TAG, "onDownloadStart:url=" + url);
                LogUtils.i(TAG, "onDownloadStart:userAgent=" + userAgent);
                LogUtils.i(TAG, "onDownloadStart:contentDisposition=" + contentDisposition);
                LogUtils.i(TAG, "onDownloadStart:mimeType=" + mimeType);
                LogUtils.i(TAG, "onDownloadStart:contentLength=" + contentLength);
                if (!allowOpenDownload) return;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    mWebView.getContext().startActivity(intent);
                } catch (Exception ignore) {
                }
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
                HitResult result = new HitResult(hitTestResult);
                switch (result.getType()) {
                    case IMAGE_TYPE:
                    case IMAGE_ANCHOR_TYPE:
                    case SRC_IMAGE_ANCHOR_TYPE:
                        new ImagePreviewDialog(mActivity, result.getResult()).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mWebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (isProgressShown) return;
                    float percent = getPercent();
                    if (mOnPageScrollChangeListener != null) {
                        mOnPageScrollChangeListener.onPageScrolled(percent);
                    }
                    if (!isPageScrollEnd && percent >= 0.95) {
                        isPageScrollEnd = true;
                        if (mOnPageScrollEndListener != null) {
                            mOnPageScrollEndListener.onPageScrollEnd();
                        }
                    }
                }
            });
        }
        WebSettings webSetting = mWebView.getSettings();
        mUserAgentString = webSetting.getUserAgentString();
        boolean isAppDarkMode = DarkModeUtils.isDarkMode(activity);
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            View v = mWebView.getView();
            if (v instanceof android.webkit.WebView) {
                android.webkit.WebView wv = (android.webkit.WebView) v;
                if (isAppDarkMode) {
                    WebSettingsCompat.setForceDark(wv.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                } else {
                    WebSettingsCompat.setForceDark(wv.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                }
            }
        } else {
            mWebView.setDayOrNight(!isAppDarkMode);
        }
        jsInjector = new JsInjector(mWebView);
        jsInjector.attach();
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

    @FloatRange(from = 0.0, to = 1.0)
    public float getPercent() {
        if (isProgressShown) return 0f;
        float contentHeight = mWebView.getContentHeight() * mWebView.getScale();
        if (contentHeight <= 0) return 0f;
        float webViewScrollY = mWebView.getWebScrollY();
        float webViewHeight = mWebView.getHeight();
        float percent = (float) (webViewScrollY + webViewHeight) / (float) contentHeight;
        percent = Math.max(0F, percent);
        percent = Math.min(1F, percent);
        return percent;
    }

    public void getShareInfo(OnShareInfoCallback callback) {
        String js = "javascript:(" +
                "function getShareInfo() {" +
                "  var map = {};" +
                "  map[\"title\"] = document.title;" +
                "  map[\"desc\"] = document.querySelector('meta[name=\"description\"]').getAttribute('content');" +
                "  var imgElements = document.getElementsByTagName(\"img\");" +
                "  var imgs = [];" +
                "  for(var i = 0 ; i < imgElements.length; i++){" +
                "    var imgEle = imgElements[i];" +
                "    var w = imgEle.naturalWidth;" +
                "    var h = imgEle.naturalHeight;" +
                "    console.log(\"img\" + i + \" w*h=\" + w + \"*\" + h);" +
                "    if(w > 200 && h > 100){" +
                "      imgs.push(imgEle.src);" +
                "    }" +
                "  }" +
                "  map[\"imgs\"] = imgs;" +
                "  return map;" +
                "}" +
                ")()";
        mWebView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                String title = "";
                String desc = "";
                List<String> imgs = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    title = jsonObject.optString("title");
                    desc = jsonObject.optString("desc");
                    JSONArray imgArr = jsonObject.optJSONArray("imgs");
                    if (imgArr != null) {
                        for (int i = 0; i < imgArr.length(); i++) {
                            String img = imgArr.optString(i);
                            if (!TextUtils.isEmpty(img)) {
                                if (!imgs.contains(img)) {
                                    imgs.add(img);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(title)) {
                    title = getTitle();
                }
                if (TextUtils.isEmpty(desc)) {
                    desc = getUrl();
                }
                if (callback != null) {
                    callback.onShareInfo(getUrl(), imgs, title, desc);
                }
            }
        });
    }

    @NonNull
    public String getTitle() {
        String title = mWebView.getTitle();
        return title == null ? "" : title;
    }

    @Nullable
    public WebView.HitTestResult getHitTestResult() {
        return mWebView.getHitTestResult();
    }

    @NonNull
    public String getUserAgent() {
        return mUserAgentString;
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

    public void goTop() {
        String js = "javascript:(" +
                "function(){\n" +
                "  var timer = null;\n" +
                "  cancelAnimationFrame(timer);\n" +
                "  var startTime = +new Date();     \n" +
                "  var b = document.body.scrollTop || document.documentElement.scrollTop;\n" +
                "  var d = 500;\n" +
                "  var c = b;\n" +
                "  var timer = requestAnimationFrame(function func(){\n" +
                "    var t = d - Math.max(0,startTime - (+new Date()) + d);\n" +
                "    document.documentElement.scrollTop = document.body.scrollTop = t * (-c) / d + b;\n" +
                "    timer = requestAnimationFrame(func);\n" +
                "    if(t == d){\n" +
                "      cancelAnimationFrame(timer);\n" +
                "    }\n" +
                "  });\n" +
                "}" +
                ")()";
        mWebView.evaluateJavascript(js, null);
    }

    public void onResume() {
        mWebView.resumeTimers();
        mWebView.onResume();
    }

    public void onPause() {
        mWebView.pauseTimers();
        mWebView.onPause();
    }

    public void onDestroy(boolean destroyOrRecycle) {
        jsInjector.detach();
        mProgressBar.clearAnimation();
        if (useInstanceCache) {
            if (destroyOrRecycle) {
                WebInstance.getInstance(mActivity).destroy(mWebView);
            } else {
                WebInstance.getInstance(mActivity).recycle(mWebView);
            }
        } else {
            WebInstance.getInstance(mActivity).destroy(mWebView);
        }
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

    public WebHolder setLoadCacheElseNetwork(boolean loadCacheElseNetwork) {
        WebSettings webSetting = mWebView.getSettings();
        if (loadCacheElseNetwork) {
            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        return this;
    }

    public WebHolder setUseInstanceCache(boolean useInstanceCache) {
        this.useInstanceCache = useInstanceCache;
        return this;
    }

    public WebHolder setAllowOpenOtherApp(boolean allowOpenOtherApp) {
        this.allowOpenOtherApp = allowOpenOtherApp;
        return this;
    }

    public WebHolder setAllowOpenDownload(boolean allowOpenDownload) {
        this.allowOpenDownload = allowOpenDownload;
        return this;
    }

    public WebHolder setAllowRedirect(boolean allowRedirect) {
        this.allowRedirect = allowRedirect;
        return this;
    }

    public WebHolder setOnPageTitleCallback(OnPageTitleCallback onPageTitleCallback) {
        mOnPageTitleCallback = onPageTitleCallback;
        return this;
    }

    public WebHolder setOnPageScrollChangeListener(OnPageScrollChangeListener mOnPageScrollChangeListener) {
        this.mOnPageScrollChangeListener = mOnPageScrollChangeListener;
        return this;
    }

    public WebHolder setOnPageScrollEndListener(OnPageScrollEndListener onPageScrollEndListener) {
        mOnPageScrollEndListener = onPageScrollEndListener;
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
            isPageScrollEnd = false;
            jsInjector.onProgressChanged(newProgress);
            if (newProgress < 30) {
                if (!isProgressShown) {
                    isProgressShown = true;
                    onShowProgress();
                }
                onProgressChanged(newProgress);
            } else if (newProgress > 80) {
                onProgressChanged(newProgress);
                if (isProgressShown) {
                    isProgressShown = false;
                    onHideProgress();
                }
            } else {
                onProgressChanged(newProgress);
            }
        }

        private void onShowProgress() {
            showProgress();
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
            hideProgress();
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

        private void showProgress() {
            mProgressBar.animate()
                    .alpha(1F)
                    .setDuration(200)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            setProgress(0);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
        }

        private void hideProgress() {
            mProgressBar.animate()
                    .alpha(0F)
                    .setDuration(200)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setProgress(100);
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
        }

        private DecorLayer mCustomViewLayer = null;
        private IX5WebChromeClient.CustomViewCallback mCustomViewCallback = null;
        private int mOldActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            if (mCustomViewLayer != null) {
                mCustomViewLayer.dismiss();
                mCustomViewLayer = null;
            }
            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }
            mCustomViewCallback = customViewCallback;
            mCustomViewLayer = new DecorLayer(mActivity);
            mCustomViewLayer.setLevel(Integer.MAX_VALUE);
            mCustomViewLayer.setAnimator(new NullAnimatorCreator());
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            mCustomViewLayer.setChild(view);
            mCustomViewLayer.show();
            mOldActivityOrientation = mActivity.getRequestedOrientation();
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomViewLayer != null) {
                mCustomViewLayer.dismiss();
                mCustomViewLayer = null;
            }
            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }
            mActivity.setRequestedOrientation(mOldActivityOrientation);
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public class WanWebViewClient extends WebViewClient {
        private WebResourceResponse shouldInterceptRequest(@NonNull Uri reqUri,
                                                           @Nullable Map<String, String> reqHeaders,
                                                           @Nullable String reqMethod) {
            String url = reqUri.toString();
            LogUtils.d(TAG, "shouldInterceptRequest:url=" + url);
            LogUtils.d(TAG, "shouldInterceptRequest:headers=" + reqHeaders);
            LogUtils.d(TAG, "shouldInterceptRequest:method=" + reqMethod);
            syncCookiesForWanAndroid(url);
            if (mInterceptUrlInterceptor == null) return null;
            return mInterceptUrlInterceptor.onInterceptUrl(reqUri, reqHeaders, reqMethod);
        }

        private boolean shouldOverrideUrlLoading(WebView view, Uri uri) {
            LogUtils.i(TAG, "shouldOverrideUrlLoading=" + uri.toString());
            String url = view.getUrl();
            String originalUrl = view.getOriginalUrl();
            WebView.HitTestResult hit = view.getHitTestResult();
            if (hit.getType() == WebView.HitTestResult.UNKNOWN_TYPE || TextUtils.isEmpty(hit.getExtra())) {
                LogUtils.i(TAG, "重定向:url=" + url);
                LogUtils.i(TAG, "重定向:originalUrl=" + originalUrl);
                if (!allowRedirect) {
                    if (!TextUtils.isEmpty(originalUrl) && (originalUrl.startsWith("http://") || originalUrl.startsWith("https://"))) {
                        return true;
                    }
                }
            }
            String scheme = uri.getScheme();
            if (!(TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https"))) {
                if (allowOpenOtherApp) {
                    try {
                        Context context = view.getContext();
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            if (mOverrideUrlInterceptor != null) {
                if (mOverrideUrlInterceptor.onOverrideUrl(uri.toString())) {
                    return true;
                }
            }
            switch (SettingUtils.getInstance().getUrlInterceptType()) {
                case HostInterceptUtils.TYPE_ONLY_WHITE:
                    if (!HostInterceptUtils.isWhiteHost(uri.getHost())) {
                        return true;
                    }
                    break;
                case HostInterceptUtils.TYPE_INTERCEPT_BLACK:
                    if (HostInterceptUtils.isBlackHost(uri.getHost())) {
                        return true;
                    }
                    break;
                case HostInterceptUtils.TYPE_NOTHING:
                default:
                    break;
            }
            return false;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Uri reqUri = Uri.parse(url);
            WebResourceResponse response = shouldInterceptRequest(reqUri, null, null);
            if (response != null) {
                return response;
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Uri reqUri = request.getUrl();
            if (reqUri == null) {
                return super.shouldInterceptRequest(view, request);
            }
            Map<String, String> reqHeaders = request.getRequestHeaders();
            String reqMethod = request.getMethod();
            WebResourceResponse response = shouldInterceptRequest(reqUri, reqHeaders, reqMethod);
            if (response != null) {
                return response;
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request, Bundle bundle) {
            Uri reqUri = request.getUrl();
            if (reqUri == null) {
                return super.shouldInterceptRequest(view, request, bundle);
            }
            Map<String, String> reqHeaders = request.getRequestHeaders();
            String reqMethod = request.getMethod();
            WebResourceResponse response = shouldInterceptRequest(reqUri, reqHeaders, reqMethod);
            if (response != null) {
                return response;
            } else {
                return super.shouldInterceptRequest(view, request, bundle);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return shouldOverrideUrlLoading(view, Uri.parse(url));
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            jsInjector.onPageStarted();
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

    public interface OnShareInfoCallback {
        void onShareInfo(@NonNull String url,
                         @NonNull List<String> covers,
                         @NonNull String title,
                         @NonNull String desc);
    }

    public interface OnPageScrollChangeListener {
        void onPageScrolled(@FloatRange(from = 0.0, to = 1.0) float percent);
    }

    public interface OnPageScrollEndListener {
        void onPageScrollEnd();
    }

    public interface OnLongClickHitTestResult {
        boolean onHitTestResult(@NonNull HitResult result);
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
        WebResourceResponse onInterceptUrl(@NonNull Uri reqUri,
                                           @Nullable Map<String, String> reqHeaders,
                                           @Nullable String reqMethod);
    }
}