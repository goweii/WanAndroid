package per.goweii.wanandroid.utils.web.cache

import android.net.Uri
import android.util.LruCache
import com.jakewharton.disklrucache.DiskLruCache
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import per.goweii.basic.utils.coder.MD5Coder
import per.goweii.basic.utils.file.CacheUtils
import per.goweii.wanandroid.utils.web.interceptor.WebHttpClient
import per.goweii.wanandroid.utils.web.interceptor.WebResUrlInterceptor.resp
import java.io.File
import java.io.IOException

object ResCacheManager {
    private const val memoryMaxSize = 100
    private const val diskMaxSize = 10 * 1024 * 1024L

    private val memoryLruCache: LruCache<String, String> = LruCache(memoryMaxSize)
    private var diskLruCache: DiskLruCache? = null

    private val supportExts = arrayListOf(
        "css",
    )

    private fun openDiskLruCache() {
        diskLruCache?.let {
            if (it.isClosed) {
                diskLruCache = null
            }
        }
        if (diskLruCache == null) {
            val file = File(CacheUtils.getCacheDir(), "web/res")
            if (!file.exists()) {
                file.mkdirs()
            }
            try {
                diskLruCache = DiskLruCache.open(file, 1, 1, diskMaxSize)
            } catch (e: IOException) {
            }
        }
    }

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
        val url = uri.toString()
        supportExts.find { it.equals(ext, true) } ?: return null
        var res = getFromCache(url)
        if (res.isNullOrEmpty()) {
            res = WebHttpClient.request(url, userAgent, reqHeaders, reqMethod).resp()
            if (!res.isNullOrEmpty()) {
                saveToCache(url, res)
            }
        }
        return res?.toWebResourceResponse("text/$ext")
    }

    private fun String.toWebResourceResponse(mimeType: String): WebResourceResponse {
        return WebResourceResponse(
            mimeType,
            Charsets.UTF_8.name(),
            this.byteInputStream(Charsets.UTF_8)
        )
    }

    private fun getFromCache(url: String): String? {
        memoryLruCache.get(url)?.let {
            if (it.isNotEmpty()) {
                return it
            } else {
                memoryLruCache.remove(url)
            }
        }
        synchronized(this) {
            openDiskLruCache()
            val diskLruCache = diskLruCache ?: return@synchronized
            val key = MD5Coder.encode(url)
            try {
                diskLruCache.get(key)?.let {
                    val res = it.getString(0)
                    if (res.isNotEmpty()) {
                        memoryLruCache.put(url, res)
                        return res
                    }
                }
            } catch (e: IOException) {
            }
        }
        return null
    }

    private fun saveToCache(url: String, res: String) {
        memoryLruCache.put(url, res)
        synchronized(this) {
            openDiskLruCache()
            val diskLruCache = diskLruCache ?: return@synchronized
            val key = MD5Coder.encode(url)
            try {
                val editor: DiskLruCache.Editor = diskLruCache.edit(key)
                editor[0] = res
                editor.commit()
                diskLruCache.flush()
            } catch (e: IOException) {
            }
        }
    }
}