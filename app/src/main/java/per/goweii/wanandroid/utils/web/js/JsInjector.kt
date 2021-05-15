package per.goweii.wanandroid.utils.web.js

import android.os.Handler
import android.os.Message
import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class JsInjector(
        private val webView: WebView
) : Handler() {
    private val JS_NAME = JsInjector::class.java.simpleName

    private val jsInterfaces = mutableListOf<BaseJsInterface>()

    @Volatile
    private var injected = false

    fun attach() {
        webView.addJavascriptInterface(this, JS_NAME)
    }

    fun detach() {
        removeCallbacksAndMessages(null)
    }

    fun addJsInterface(jsInterface: BaseJsInterface) {
        webView.addJavascriptInterface(jsInterface, jsInterface.jsName)
        jsInterface.attach(webView)
        jsInterfaces.add(jsInterface)
    }

    fun onPageStarted() {
        injected = false
        removeCallbacksAndMessages(null)
    }

    fun onProgressChanged(p: Int) {
        if (injected) return
        checkHtmlLoadFinish()
    }

    private fun checkHtmlLoadFinish() {
        webView.loadUrl("""
                javascript: window.$JS_NAME.htmlBody(document.getElementsByTagName('body')[0].innerHTML);
            """.trimIndent())
    }

    @JavascriptInterface
    fun htmlBody(body: String?) {
        if (body.isNullOrEmpty()) return
        sendEmptyMessage(0)
    }

    override fun handleMessage(msg: Message) {
        removeCallbacksAndMessages(null)
        if (injected) return
        injected = true
        inject()
    }

    private fun inject() {
        jsInterfaces.forEach {
            it.inject()
        }
    }
}