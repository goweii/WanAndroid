package per.goweii.wanandroid.utils.web.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import per.goweii.wanandroid.utils.web.WebInstance
import per.goweii.wanandroid.utils.web.view.ResuableWebView
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WebFetch(private val context: Context) {
    companion object {
        private const val TAG = "WebFetch"

        private const val FETCH_JS =
            """javascript:(
function getShareInfo() {
var map = {};
map["title"] = document.title;
map["desc"] = document.querySelector('meta[name="description"]').getAttribute('content');
var imgElements = document.getElementsByTagName("img");
var imgs = [];
for(var i = 0 ; i < imgElements.length; i++){
  var imgEle = imgElements[i];
  var w = imgEle.naturalWidth;
  var h = imgEle.naturalHeight;
  if(w > 200 && h > 100) {
    imgs.push(imgEle.src);
  }
}
map["imgs"] = imgs;
map["html"] = document.documentElement.outerHTML;
return map;
}
)()"""

        @SuppressLint("StaticFieldLeak")
        private var instance: WebFetch? = null

        fun getInstance(context: Context): WebFetch {
            if (instance == null) {
                synchronized(WebFetch::class) {
                    if (instance == null) {
                        instance = WebFetch(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    suspend fun fetch(url: String): WebFetchResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val webView = prepareWebView()
                webView.webChromeClient = object : WebChromeClient() {}
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (continuation.isActive) {
                            webView.fetch(
                                onSuccess = { e ->
                                    if (continuation.isActive) {
                                        continuation.resume(e)
                                    }
                                },
                                onFailure = { e ->
                                    if (continuation.isActive) {
                                        continuation.resumeWithException(e)
                                    }
                                },
                            )
                        }
                    }
                }
                webView.loadUrl(url)
                continuation.invokeOnCancellation {
                    WebInstance.getInstance(context).recycle(webView)
                }
            }
        }
    }

    private fun WebView.fetch(
        onSuccess: (WebFetchResult) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        evaluateJavascript(FETCH_JS) { value ->
            try {
                val jsonObject = JSONObject(value)
                val title = jsonObject.optString("title") ?: ""
                val desc = jsonObject.optString("desc") ?: ""
                val imgs = jsonObject.optJSONArray("imgs")?.let { arr ->
                    val imgs = arrayListOf<String>()
                    for (i in 0..<arr.length()) {
                        val img = arr.optString(i)
                        if (!TextUtils.isEmpty(img)) {
                            if (!imgs.contains(img)) {
                                imgs.add(img)
                            }
                        }
                    }
                    return@let imgs
                } ?: emptyList()
                var html = jsonObject.optString("html") ?: ""
                if (html.startsWith("\"") && html.endsWith("\"")) {
                    html = html.substring(1, html.length - 1)
                    html = html.replace("\\u003C", "<")
                        .replace("\\u003E", ">")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\")
                }
                onSuccess(
                    WebFetchResult(
                        title = title,
                        desc = desc,
                        images = imgs,
                        html = html,
                    )
                )
            } catch (e: JSONException) {
                onFailure(e)
            }
        }
    }

    private fun prepareWebView(): ResuableWebView {
        val webView = WebInstance.getInstance(context).obtain(context)
        val width = 1080
        val height = 1920
        webView.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        webView.layout(0, 0, width, height)
        return webView
    }

    data class WebFetchResult(
        val title: String,
        val desc: String,
        val images: List<String>,
        val html: String,
    )
}