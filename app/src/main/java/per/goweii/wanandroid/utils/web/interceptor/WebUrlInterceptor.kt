package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
interface WebUrlInterceptor {
    fun intercept(uri: Uri,
                  userAgent: String?,
                  reqHeaders: Map<String, String>?,
                  reqMethod: String?): WebResourceResponse?

    fun isSupportNightMode() = false
}