package per.goweii.wanandroid.http

import android.net.Uri
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import per.goweii.basic.utils.LogUtils
import java.io.IOException

class GoweiiHostInterceptor : Interceptor {
    companion object {
        private const val TAG = "GoweiiHostInterceptor"

        private const val GOWEII_HOST = "goweii"

        private const val GITEE_PAGE_SERVER_BASE_URL = "https://goweii.gitee.io/wanandroidserver"
        private const val GITEE_RAW_SERVER_BASE_URL = "https://gitee.com/goweii/WanAndroidServer/raw/master"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val httpUrl = request.url()
        if (httpUrl.host() != GOWEII_HOST) {
            return chain.proceed(request)
        }

        HttpUrl.parse(GITEE_RAW_SERVER_BASE_URL)
            ?.let { chain.tryWithBaseUrl(it) }
            ?.takeIf { it.isSuccessful }
            ?.let { return it }

        HttpUrl.parse(GITEE_PAGE_SERVER_BASE_URL)
            ?.let { chain.tryWithBaseUrl(it) }
            ?.takeIf { it.isSuccessful }
            ?.let { return it }

        throw IOException("Unknown goweii host.")
    }

    private fun Interceptor.Chain.tryWithBaseUrl(baseUrl: HttpUrl): Response {
        val httpUrl = request().url().replaceWithBaseUrl(baseUrl)
        LogUtils.d(TAG, "tryWithBaseUrl: $httpUrl")
        return proceed(request().newBuilder().url(httpUrl).build())
    }

    private fun HttpUrl.replaceWithBaseUrl(baseUrl: HttpUrl): HttpUrl {
        val uriBuilder = Uri.parse(baseUrl.toString()).buildUpon()
        encodedPathSegments().forEach { uriBuilder.appendEncodedPath(it) }
        uriBuilder.encodedQuery(encodedQuery())
        val uri = uriBuilder.build()
        return HttpUrl.parse(uri.toString())!!
    }
}