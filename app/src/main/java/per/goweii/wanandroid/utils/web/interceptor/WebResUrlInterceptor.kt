package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.wanandroid.utils.web.cache.ImageCacheManager
import per.goweii.wanandroid.utils.web.cache.ResCacheManager

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
object WebResUrlInterceptor : BaseWebUrlInterceptor() {
    override fun intercept(
        pageUri: Uri,
        uri: Uri,
        userAgent: String?,
        reqHeaders: Map<String, String>?,
        reqMethod: String?
    ): WebResourceResponse? {
        ResCacheManager.get(uri, userAgent, reqHeaders, reqMethod)?.let { return it }
        ImageCacheManager.get(uri, userAgent, reqHeaders, reqMethod)?.let { return it }
        return null
    }
}