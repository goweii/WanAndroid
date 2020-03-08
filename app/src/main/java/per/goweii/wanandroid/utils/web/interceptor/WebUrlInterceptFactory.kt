package per.goweii.wanandroid.utils.web.interceptor

import android.net.Uri

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
enum class WebUrlInterceptFactory(
        val interceptor: WebUrlInterceptor
) {
    JIANSHU(JianshuWebUrlInterceptor()),
    JUEJIN(JuejinWebUrlInterceptor());

    companion object {
        fun create(uri: Uri): WebUrlInterceptFactory? {
            return when (uri.host) {
                "www.jianshu.com" -> JIANSHU
                "juejin.im" -> JUEJIN
                else -> null
            }
        }
    }

}