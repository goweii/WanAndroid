package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.wanandroid.utils.web.cache.ResCacheManager
import java.io.ByteArrayInputStream

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
object WebResUrlInterceptor : BaseWebUrlInterceptor() {
    override fun intercept(pageUri: Uri,
                           uri: Uri,
                           userAgent: String?,
                           reqHeaders: Map<String, String>?,
                           reqMethod: String?): WebResourceResponse? {
        val url = uri.toString()
        if (url.endsWith(".js", true)) {
            val res = getRes(url, userAgent, reqHeaders, reqMethod)
            if (!res.isNullOrEmpty()) {
                return WebResourceResponse("application/javascript", "utf-8", ByteArrayInputStream(res.toByteArray()))
            }
        }
        if (url.endsWith(".css", true)) {
            val res = getRes(url, userAgent, reqHeaders, reqMethod)
            if (!res.isNullOrEmpty()) {
                return WebResourceResponse("text/css", "utf-8", ByteArrayInputStream(res.toByteArray()))
            }
        }
        return null
    }

    private fun getRes(url: String,
                       userAgent: String?,
                       reqHeaders: Map<String, String>?,
                       reqMethod: String?): String? {
        var res = ResCacheManager.get(url)
        if (res.isNullOrEmpty()) {
            res = WebHttpClient.request(url, userAgent, reqHeaders, reqMethod).resp()
            if (!res.isNullOrEmpty()) {
                ResCacheManager.save(url, res)
            }
        }
        return res
    }
}