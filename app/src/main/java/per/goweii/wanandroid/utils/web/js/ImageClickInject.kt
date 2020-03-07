package per.goweii.wanandroid.utils.web.js

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.webkit.JavascriptInterface
import per.goweii.wanandroid.module.main.dialog.ImagePreviewDialog


class ImageClickInject : BaseInject("IMAGE_CLICK_INJECT") {

    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val url = msg.obj as String
            ImagePreviewDialog(webView.context, url).show()
        }
    }

    override fun injectOnProgress(): IntArray = intArrayOf(90)
    override fun loadJsFromAssets(): String? = null
    override fun loadJsFromString(): String? = """
        (function(){
            var imgs = document.getElementsByTagName("img");
            for(var i=0; i < imgs.length; i++){
                imgs[i].onclick = function(){
                    window.IMAGE_CLICK_INJECT.previewImage(this.src);
                }
            }
        })()
    """.trimIndent()

    override fun loadJsInitCode(): String? = null

    @JavascriptInterface
    fun previewImage(url: String) {
        val msg = mainHandler.obtainMessage()
        msg.obj = url
        mainHandler.sendMessage(msg)
    }
}