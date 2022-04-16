package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.wanandroid.utils.web.cache.HtmlCacheManager
import per.goweii.wanandroid.utils.web.cache.ReadingModeManager
import per.goweii.wanandroid.utils.web.css.CssStyleManager
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

object WebReadingModeInterceptor : BaseWebUrlInterceptor() {
    override fun intercept(
        pageUri: Uri,
        uri: Uri,
        userAgent: String?,
        reqHeaders: Map<String, String>?,
        reqMethod: String?
    ): WebResourceResponse? {
        if (reqMethod != "GET") return null
        val urlRegexBean = ReadingModeManager.getUrlRegexBeanForHost(uri.host) ?: return null
        val url = uri.toString()
        val pattern = Pattern.compile(urlRegexBean.regex)
        val matcher = pattern.matcher(url)
        if (!matcher.find()) {
            return null
        }
        var html = HtmlCacheManager.get(url)
        if (html == null) {
            html = WebHttpClient.request(url, userAgent, reqHeaders, reqMethod).resp()?.also {
                HtmlCacheManager.save(url, it)
            }
        }
        html ?: return null
        if (html.isNotEmpty()) {
            // val style = Element("style")
            //     .attr("type", "text/css")
            //     .text(CssStyleManager.get(urlRegexBean.name))
            // val doc = Jsoup.parse(html, url)
            // doc.head().appendChild(style)
            // html = doc.html() ?: return null
            html = CssStyleManager.appendCssOnFirstStyle(html, urlRegexBean.name)
        }
        return WebResourceResponse("text/html", "utf-8", ByteArrayInputStream(html.toByteArray()))
    }
}