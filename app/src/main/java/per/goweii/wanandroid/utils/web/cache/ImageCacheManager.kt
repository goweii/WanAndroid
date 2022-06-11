package per.goweii.wanandroid.utils.web.cache

import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.basic.utils.Utils
import java.io.InputStream

object ImageCacheManager {
    private val supportExts = arrayListOf(
        "jpg", "jpeg",
        "png", "apng",
        "webp", "awebp",
        "gif",
    )

    fun get(
        uri: Uri,
        userAgent: String?,
        reqHeaders: Map<String, String>?,
        reqMethod: String?
    ): WebResourceResponse? {
        val lastPathSegment = uri.lastPathSegment ?: return null
        val lastIndexOf = lastPathSegment.lastIndexOf(".")
        if (lastIndexOf == -1) return null
        val ext = lastPathSegment.substring(lastIndexOf + 1)
        if (ext.isBlank()) return null
        supportExts.find { it.equals(ext, true) } ?: return null
        return Glide.with(Utils.getAppContext()).asFile()
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
            .load(uri.toString()).submit().get()
            .inputStream()
            .toWebResourceResponse("image/$ext")
    }

    private fun InputStream.toWebResourceResponse(mimeType: String): WebResourceResponse {
        return WebResourceResponse(mimeType, Charsets.UTF_8.name(), this)
    }
}