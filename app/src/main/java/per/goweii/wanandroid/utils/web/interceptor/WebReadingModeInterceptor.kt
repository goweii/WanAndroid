package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.rxhttp.request.RxRequest
import per.goweii.wanandroid.http.WanApi
import per.goweii.wanandroid.module.main.model.WebArticleUrlRegexBean
import per.goweii.wanandroid.utils.web.cache.HtmlCacheManager
import per.goweii.wanandroid.utils.web.css.CssStyleManager
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

object WebReadingModeInterceptor : BaseWebUrlInterceptor() {
    private val urlRegexList = arrayListOf<WebArticleUrlRegexBean>()

    fun setup() {
        RxRequest.create(WanApi.api().webArticleUrlRegex)
                .request(object : RxRequest.ResultCallback<List<WebArticleUrlRegexBean>> {
                    override fun onSuccess(code: Int, data: List<WebArticleUrlRegexBean>) {
                        urlRegexList.clear()
                        urlRegexList.addAll(data)
                    }

                    override fun onFailed(code: Int, msg: String) {
                    }
                })
    }

    override fun intercept(uri: Uri,
                           userAgent: String?,
                           reqHeaders: Map<String, String>?,
                           reqMethod: String?): WebResourceResponse? {
        val resp = super.intercept(uri, userAgent, reqHeaders, reqMethod)
        if (resp != null) return resp
        if (reqMethod != "GET") return null
        if (urlRegexList.isEmpty()) return null
        val host = uri.host ?: return null
        val urlRegexBean = urlRegexList
                .firstOrNull { host.contains(it.host) }
                ?: return null
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
            html = CssStyleManager.appendCssOnFirstStyle(html, urlRegexBean.name)
        }
        return WebResourceResponse("text/html", "utf-8", ByteArrayInputStream(html.toByteArray()))
    }

}