package per.goweii.wanandroid.utils.web.interceptor

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
object WebHttpClient {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    fun request(
        url: String,
        userAgent: String? = null,
        headers: Map<String, String>? = null,
        method: String? = null
    ): Call {
        val requestBuilder = Request.Builder().url(url)
        method?.let {
            requestBuilder.method(method, null)
        }
        requestBuilder.addHeader("USER-AGENT",
            userAgent
                ?: "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36"
        )
        headers?.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
        val request = requestBuilder.build()
        return okHttpClient.newCall(request)
    }

    fun Call.stringRespBody(): String? {
        return try {
            val response = execute()
            if (response.isSuccessful) {
                response.body()?.string()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}