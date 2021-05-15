package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import okhttp3.Call
import per.goweii.wanandroid.utils.web.cache.ResCacheManager
import per.goweii.wanandroid.utils.web.interceptor.WebHttpClient.stringRespBody
import java.io.ByteArrayInputStream

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
object WebResUrlInterceptor : WebUrlInterceptor {
    private val callList = arrayListOf<Call>()

    override fun intercept(uri: Uri,
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

    private fun Call.resp(): String? {
        callList.add(this)
        val resp = stringRespBody()
        callList.remove(this)
        return resp
    }

    override fun cancel() {
        callList.forEach { it.cancel() }
        callList.clear()
    }
}