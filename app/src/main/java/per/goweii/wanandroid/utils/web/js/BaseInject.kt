package per.goweii.wanandroid.utils.web.js

import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView
import per.goweii.basic.utils.DebugUtils

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
abstract class BaseInject(private val JsInterfaceName: String) {

    protected lateinit var webView: WebView private set
    private var injectedSuccess = false
    private var injectedProgress = 0

    private val debug = DebugUtils.isDebug()
    private val js: String? by lazy {
        val jsStr = loadJsFromString()
        if (!jsStr.isNullOrEmpty()) {
            return@lazy jsStr
        }
        val assetsName = loadJsFromAssets()
        if (assetsName.isNullOrEmpty()) {
            return@lazy null
        }
        try {
            webView.context.resources.assets.open(assetsName).use {
                try {
                    val buffer = ByteArray(it.available())
                    it.read(buffer)
                    String(buffer)
                } catch (e: Throwable) {
                    null
                }
            }
        } catch (e: Throwable) {
            null
        }
    }
    private val jsInitCode: String? by lazy {
        loadJsInitCode()
    }

    abstract fun loadJsFromAssets(): String?
    abstract fun loadJsFromString(): String?
    abstract fun loadJsInitCode(): String?

    open fun injectOnProgress() = intArrayOf(75)

    fun attach(webView: WebView) {
        this.webView = webView
        webView.addJavascriptInterface(this, JsInterfaceName)
    }

    fun onPageStarted() {
        injectedSuccess = false
        injectedProgress = 0
    }

    fun onProgressChanged(p: Int) {
        if (!debug) return
        if (injectedSuccess) return
        injectOnProgress().forEach {
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
        if (!js.isNullOrEmpty()) {
            webView.loadUrl("javascript: $js")
        }
        if (!jsInitCode.isNullOrEmpty()) {
            val name = this::class.java.simpleName
            webView.loadUrl("""
                javascript: var init$name = document.createElement("script");
                init$name.id= "init$name";
                init$name.text= "$jsInitCode";
                document.body.appendChild(init$name);
            """.trimIndent())
        }
        webView.loadUrl("javascript: window.${JsInterfaceName}.injectSuccess();")
    }

    @JavascriptInterface
    fun injectSuccess() {
        injectedSuccess = true
    }

}