package per.goweii.wanandroid.utils.web.interceptor

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
object WebHttpClient {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    fun request(url: String,
                userAgent: String? = null,
                headers: Map<String, String>? = null,
                method: String? = null
    ): String? {
        val requestBuilder = Request.Builder().url(url)
        headers?.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
        userAgent?.let {
            requestBuilder.addHeader("USER-AGENT", it)
        }
        method?.let {
            requestBuilder.method(method, null)
        }
        val request = requestBuilder.build()
        val response = okHttpClient.newCall(request).execute()
        return response.body()?.string()
    }

}