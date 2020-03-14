package per.goweii.wanandroid.utils.web.js

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Patterns
import android.webkit.JavascriptInterface
import per.goweii.wanandroid.module.main.dialog.ImagePreviewDialog
import java.util.regex.Matcher


class ImageClickInject : BaseInject("IMAGE_CLICK_INJECT") {

    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val url = msg.obj as String
            ImagePreviewDialog(webView.context, url).show()
            //ImageListPreviewDialog(webView.context, urls, index).show()
        }
    }

    override fun injectOnProgress(): IntArray = intArrayOf()
    override fun loadJsFromAssets(): String? = null
    override fun loadJsFromString(): String? = """
        (function(){
            var objs = document.getElementsByTagName("img");
            var imghtmls = new Array();
            for(var i = 0; i < objs.length; i++){
                imghtmls[i] = objs[i].outerHTML;
                objs[i].onclick = function(){
                    window.IMAGE_CLICK_INJECT.previewImage(this.src, this.outerHTML, imghtmls);
                }
            }
        })()
    """.trimIndent()

    override fun loadJsInitCode(): String? = null

    @JavascriptInterface
    fun previewImage(imgurl: String?, imghtml: String?, imghtmls: Array<String?>) {
        var url: String? = null
        if (isImgUrl(imgurl)) {
            url = imgurl
        } else {
            val img = findUrlFromHtml(imghtml)
            if (isImgUrl(img)) {
                url = img
            }
        }
        if (url.isNullOrEmpty()) return
        val msg = mainHandler.obtainMessage()
        msg.obj = url
        mainHandler.sendMessage(msg)
    }

    private fun isImgUrl(imgurl: String?): Boolean {
        if (imgurl.isNullOrEmpty()) return false
        return imgurl.startsWith("http")
    }

    private fun findUrlFromHtml(imghtml: String?): String? {
        if (imghtml.isNullOrEmpty()) return null
        val matcher: Matcher = Patterns.WEB_URL.matcher(imghtml)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }
}