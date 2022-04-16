package per.goweii.wanandroid.utils.web.interceptor

import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.basic.utils.Utils
import per.goweii.wanandroid.utils.web.cache.ResCacheManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
        // TODO 缓存js后，微信公众号文章懒加载图片无法加载。
        // if (url.endsWith(".js", true)) {
        //     return getRes(url, userAgent, reqHeaders, reqMethod)
        //         ?.toWebResourceResponse("text/javascript")
        // }
        val lastPathSegment = uri.lastPathSegment ?: return null
        val url = uri.toString()
        if (lastPathSegment.endsWith(".css", true)) {
            val res = getRes(url, userAgent, reqHeaders, reqMethod)
            if (!res.isNullOrEmpty()) {
                return getRes(url, userAgent, reqHeaders, reqMethod)
                    ?.toWebResourceResponse("text/css")
            }
        }
        if (lastPathSegment.endsWith(".jpg", true) || lastPathSegment.endsWith(".jpeg", true)) {
            return Glide.with(Utils.getAppContext()).asBitmap()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url).submit().get()
                .inputStream(Bitmap.CompressFormat.JPEG)
                .toWebResourceResponse("image/jpeg")
        }
        if (lastPathSegment.endsWith(".png", true) || lastPathSegment.endsWith(".apng", true)) {
            return Glide.with(Utils.getAppContext()).asBitmap()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url).submit().get()
                .inputStream(Bitmap.CompressFormat.PNG)
                .toWebResourceResponse("image/png")
        }
        if (lastPathSegment.endsWith(".webp", true) || lastPathSegment.endsWith(".awebp", true)) {
            return Glide.with(Utils.getAppContext()).asBitmap()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url).submit().get()
                .inputStream(Bitmap.CompressFormat.WEBP)
                .toWebResourceResponse("image/webp")
        }
        if (lastPathSegment.endsWith(".gif", true)) {
            return Glide.with(Utils.getAppContext()).asGif()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url).submit().get()
                .inputStream()
                .toWebResourceResponse("image/gif")
        }
        return null
    }

    private fun GifDrawable.inputStream(): InputStream {
        return ByteArrayInputStream(this.buffer.array())
    }

    private fun Bitmap.inputStream(format: Bitmap.CompressFormat): InputStream {
        val baos = ByteArrayOutputStream()
        this.compress(format, 100, baos)
        return ByteArrayInputStream(baos.toByteArray())
    }

    private fun InputStream.toWebResourceResponse(mimeType: String): WebResourceResponse {
        return WebResourceResponse(mimeType, Charsets.UTF_8.name(), this)
    }

    private fun String.toWebResourceResponse(mimeType: String): WebResourceResponse {
        return WebResourceResponse(
            mimeType,
            Charsets.UTF_8.name(),
            this.byteInputStream(Charsets.UTF_8)
        )
    }

    private fun getRes(
        url: String,
        userAgent: String?,
        reqHeaders: Map<String, String>?,
        reqMethod: String?
    ): String? {
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