package per.goweii.wanandroid.utils.web;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebSettings;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;
import java.util.List;

import per.goweii.wanandroid.widget.X5WebView;

public class WebInstance {
    private static WebInstance sInstance = null;

    private final Application mApplication;
    private final List<WebView> mCache = new ArrayList<>(1);

    private WebInstance(@NonNull Application application) {
        mApplication = application;
        WebView webView = create();
        mCache.add(webView);
    }

    @NonNull
    public static WebInstance getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new WebInstance((Application) context.getApplicationContext());
        }
        return sInstance;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @NonNull
    public WebView obtain() {
        if (mCache.isEmpty()) {
            return create();
        }
        WebView webView = mCache.remove(0);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webView.clearHistory();
        webView.resumeTimers();
        return webView;
    }

    public void recycle(@NonNull WebView webView) {
        ViewParent parent = webView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(webView);
        }
        try {
            webView.stopLoading();
            int step = 0;
            while (webView.canGoBackOrForward(step - 1)) step--;
            webView.goBackOrForward(step);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            WebSettings webSetting = webView.getSettings();
            webSetting.setJavaScriptEnabled(false);
            webView.pauseTimers();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
        } catch (Exception ignore) {
        } finally {
            if (!mCache.contains(webView)) {
                mCache.add(webView);
            }
        }
    }

    public void destroy(@NonNull WebView webView) {
        recycle(webView);
        try {
            webView.removeAllViews();
            webView.destroy();
        } catch (Exception ignore) {
        } finally {
            mCache.remove(webView);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public WebView create() {
        WebView webView = new X5WebView(mApplication);
        webView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        webView.setBackgroundColor(0);
        webView.getBackground().setAlpha(0);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.getView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings webSetting = webView.getSettings();
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
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        IX5WebSettingsExtension ext = webView.getSettingsExtension();
        if (ext != null) {
            ext.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        return webView;
    }
}
