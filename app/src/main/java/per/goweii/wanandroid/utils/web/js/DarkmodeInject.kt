package per.goweii.wanandroid.utils.web.js

import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView
import per.goweii.basic.utils.DebugUtils

class DarkmodeInject(private val webView: WebView) {

    companion object {
        private val INJECT_PROGRESS = intArrayOf()
    }

    private val debug = DebugUtils.isDebug()
    private val js: String
    private var injectedSuccess = false
    private var injectedProgress = 0

    init {
        js = webView.context.resources.assets.open("js/darkmode.js").use {
            try {
                val buffer = ByteArray(it.available())
                it.read(buffer)
                String(buffer)
            } catch (e: Throwable) {
                ""
            }
        }
        webView.addJavascriptInterface(this, "DARKMODE_INJECT")
    }

    fun onPageStarted() {
        injectedSuccess = false
        injectedProgress = 0
    }

    fun onProgressChanged(p: Int) {
        if (!debug) return
        if (injectedSuccess) return
        INJECT_PROGRESS.forEach {
            if (p >= it) {
                if (it > injectedProgress) {
                    injectedProgress = p
                    inject()
                    return@forEach
                }
            }
        }
    }

    private fun inject() {
        webView.loadUrl("javascript: $js")
        val darkmodeInitStr = """
            var options = {
              bottom: '64px',
              right: 'unset',
              left: '32px',
              time: '0.5s',
              mixColor: '#fff',
              backgroundColor: '#fff',
              buttonColorDark: '#100f2c',
              buttonColorLight: '#fff',
              saveInCookies: false,
              label: '🌓',
              autoMatchOsTheme: true
            }
            const darkmode = new Darkmode();
            if(!darkmode.isActivated()){
              darkmode.toggle();
            }
        """.trimIndent()
        webView.loadUrl("""
            javascript: var darkmodeInit = document.createElement("script");
            darkmodeInit.id= "darkmode_init";
            darkmodeInit.text= "const darkmode = new Darkmode();if(!darkmode.isActivated()){darkmode.toggle();}";
            document.body.appendChild(darkmodeInit);
            window.DARKMODE_INJECT.injectSuccess();
        """.trimIndent())
    }

    @JavascriptInterface
    fun injectSuccess() {
        injectedSuccess = true
    }

}