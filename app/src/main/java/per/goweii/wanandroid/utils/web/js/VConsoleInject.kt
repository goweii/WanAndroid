package per.goweii.wanandroid.utils.web.js

import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView
import per.goweii.basic.utils.DebugUtils

class VConsoleInject(private val webView: WebView) {
    private val debug = DebugUtils.isDebug()
    private val js: String
    private var injectedSuccess = false
    private var injectedProgress = 0

    companion object {
        private val INJECT_PROGRESS = intArrayOf(30, 50, 75)
    }

    init {
        js = webView.context.resources.assets.open("js/vconsole.min.js").use {
            try {
                val buffer = ByteArray(it.available())
                it.read(buffer)
                String(buffer)
            } catch (e: Throwable) {
                ""
            }
        }
        webView.addJavascriptInterface(this, "VCONSOLE_INJECT")
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
        webView.loadUrl("""
            javascript: var vConsoleInit = document.createElement("script");
            vConsoleInit.id= "vconsole_init";
            vConsoleInit.text= "var vConsole = new VConsole();";
            document.body.appendChild(vConsoleInit);
            window.VCONSOLE_INJECT.injectSuccess();
        """.trimIndent())
    }

    @JavascriptInterface
    fun injectSuccess() {
        injectedSuccess = true
    }

}