package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import android.webkit.WebResourceResponse

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
interface WebUrlInterceptor {
    fun intercept(pageUri: Uri,
                  uri: Uri,
                  userAgent: String?,
                  reqHeaders: Map<String, String>?,
                  reqMethod: String?): WebResourceResponse?

    fun cancel()
}