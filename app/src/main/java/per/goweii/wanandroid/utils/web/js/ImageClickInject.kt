package per.goweii.wanandroid.utils.web.js

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Patterns
import android.webkit.JavascriptInterface
import per.goweii.wanandroid.module.main.dialog.ImagePreviewDialog
import java.util.regex.Matcher


class ImageClickInject : BaseInject("IMAGE_CLICK_INJECT") {

    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val urls = msg.obj as List<String>
            val index = msg.arg1
            ImagePreviewDialog(webView.context, urls[index]).show()
            //ImageListPreviewDialog(webView.context, urls, index).show()
        }
    }

    override fun injectOnProgress(): IntArray = intArrayOf(90)
    override fun loadJsFromAssets(): String? = null
    override fun loadJsFromString(): String? = """
        (function(){
            var objs = document.getElementsByTagName("img");
            var imghtmls = new Array();
            for(var i = 0; i < objs.length; i++){
                imghtmls[i] = objs[i].outerHTML;
                objs[i].onclick = function(){
                    window.IMAGE_CLICK_INJECT.previewImage(this.outerHTML, imghtmls);
                }
            }
        })()
    """.trimIndent()

    override fun loadJsInitCode(): String? = null

    @JavascriptInterface
    fun previewImage(imghtml: String, imghtmls: Array<String>) {
        val imgList = arrayListOf<String>()
        val img = findUrlFromHtml(imghtml)
        for (ih in imghtmls) {
            findUrlFromHtml(ih)?.let {
                imgList.add(it)
            }
        }
        var index = 0
        img?.let {
            imgList.forEachIndexed { i, s ->
                if (TextUtils.equals(img, s)) {
                    index = i
                    return@forEachIndexed
                }
            }
        }
        val msg = mainHandler.obtainMessage()
        msg.arg1 = index
        msg.obj = imgList
        mainHandler.sendMessage(msg)
    }

    private fun findUrlFromHtml(imghtml: String): String? {
        val matcher: Matcher = Patterns.WEB_URL.matcher(imghtml)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }
}