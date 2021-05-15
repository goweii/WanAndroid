package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import okhttp3.Call
import per.goweii.wanandroid.utils.web.interceptor.WebHttpClient.stringRespBody

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
abstract class BaseWebUrlInterceptor : WebUrlInterceptor {

    private val callList = arrayListOf<Call>()

    override fun intercept(uri: Uri,
                           userAgent: String?,
                           reqHeaders: Map<String, String>?,
                           reqMethod: String?): WebResourceResponse? {
        return null
    }

    protected fun Call.resp(): String? {
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