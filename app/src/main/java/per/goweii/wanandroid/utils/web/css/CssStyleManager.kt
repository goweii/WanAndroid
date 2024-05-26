package per.goweii.wanandroid.utils.web.css

import per.goweii.basic.utils.file.CacheUtils
import per.goweii.wanandroid.common.Constant
import per.goweii.wanandroid.utils.web.interceptor.WebHttpClient
import java.io.File
import java.util.regex.Pattern

/**
 * @author CuiZhen
 * @date 2020/7/12
 */
object CssStyleManager {
    private const val headRegex = "(<head[\\s\\S]*?>)([\\s\\S]*?)(</head>)"
    private const val scriptRegex = "(<script[\\s\\S]*?>)([\\s\\S]*?)(</script>)"
    private const val styleRegex = "(<style[\\s\\S]*?>)([\\s\\S]*?)(</style>)"
    private const val url = "https://goweii/web/css/"

    private val disableJsModifyCssScript = """
// 将 style 属性设置为只读
Object.defineProperty(HTMLElement.prototype, 'style', {
  get: function() { 
    return this.attributes.style;
  },
  set: function(value) {
    throw new Error('CSS modification is not allowed.');
  }
});
 
// 或者监听DOM mutation events来阻止修改
document.addEventListener('DOMSubtreeModified', function(e) {
  var target = e.target;
  if (target instanceof HTMLElement && target.style !== target.getAttribute('style')) {
    throw new Error('CSS modification is not allowed.');
  }
}, true);
            """.trimIndent();

    private val cache = hashMapOf<String, String>()
    private val updateTime = hashMapOf<String, Long>()

    fun addStyle(html: String, name: String): String {
        val pattern = Pattern.compile(headRegex)
        val m = pattern.matcher(html)
        return if (m.find()) {
            val start = m.start(0)
            val end = m.end(0)
            val sb = StringBuilder()
            sb.append(html.substring(0 until start))
            sb.append(m.group(1))
            sb.append("\n")
            sb.append("<script>")
            sb.append("\n")
//            sb.append(disableJsModifyCssScript)
            sb.append("\n")
            sb.append("</script>")
            sb.append("\n")
            get(name)?.let { css ->
                sb.append("<style>")
                sb.append("\n")
                sb.append(css)
                sb.append("\n")
                sb.append("</style>")
                sb.append("\n")
            }
            sb.append(m.group(2))
            sb.append(m.group(3))
            sb.append(html.substring(end until html.length))
            sb.toString()
        } else {
            html
        }
    }

    fun disableJsModifyCss(html: String): String {
        val pattern = Pattern.compile(scriptRegex)
        val m = pattern.matcher(html)
        return if (m.find()) {
            val start = m.start(0)
            val end = m.end(0)
            val sb = StringBuilder()
            sb.append(html.substring(0 until start))
            sb.append(m.group(1))
            sb.append(m.group(2))
            sb.append("\n")
            sb.append(
                """
                Object.defineProperty(HTMLElement.prototype, 'style', {
                  get: function() { 
                    return this.attributes.style;
                  },
                  set: function(value) {
                    throw new Error('CSS modification is not allowed.');
                  }
                });
            """.trimIndent()
            )
            sb.append("\n")
            sb.append(m.group(3))
            sb.append(html.substring(end until html.length))
            sb.toString()
        } else {
            html
        }
    }

    fun appendCssOnFirstStyle(html: String, name: String): String {
        val css = get(name) ?: return html
        val pattern = Pattern.compile(styleRegex)
        val m = pattern.matcher(html)
        return if (m.find()) {
            val start = m.start(0)
            val end = m.end(0)
            val sb = StringBuilder()
            sb.append(html.substring(0 until start))
            sb.append(m.group(1))
            sb.append(m.group(2))
            sb.append("\n")
            sb.append(css)
            sb.append("\n")
            sb.append(m.group(3))
            sb.append(html.substring(end until html.length))
            sb.toString()
        } else {
            html
        }
    }

    fun get(name: String): String? {
        var css = cache[name]
        if (!css.isNullOrEmpty()) {
            return css
        }
        css = getFromFile(name)
        if (!css.isNullOrEmpty()) {
            cache[name] = css
            return css
        }
        css = getFromNet(name)
        if (!css.isNullOrEmpty()) {
            writeToFile(name, css)
            return css
        }
        return null
    }

    private fun getFromFile(name: String): String? {
        val lastTime = updateTime[name] ?: 0
        val currTime = System.currentTimeMillis()
        if (currTime - lastTime > 24 * 60 * 60 * 1000L) {
            updateTime[name] = currTime
            return null
        }
        val cssFile = getCacheFile(name)
        return try {
            cssFile.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun writeToFile(name: String, css: String) {
        val cssFile = getCacheFile(name)
        if (cssFile.exists()) {
            cssFile.delete()
        } else {
            if (cssFile.parentFile?.exists() != true) {
                cssFile.parentFile?.mkdirs()
            }
        }
        try {
            cssFile.writeText(css)
            updateTime[name] = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCacheFile(name: String): File {
        return File(CacheUtils.getFilesDir(), "web/css/${name}.css")
    }

    private fun getFromNet(name: String): String? {
        val call = WebHttpClient.request(
            url = "${url}${name}.css",
            userAgent = Constant.BROWSER_UA,
        )
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()?.string()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}