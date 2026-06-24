package per.goweii.wanandroid.utils.web.utils

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.util.data.MutableDataSet
import net.dankito.readability4j.Article
import net.dankito.readability4j.extended.Readability4JExtended
import org.jsoup.Jsoup
import kotlin.text.replace

class WebReadability(
    val url: String,
    val html: String,
    val preText: Boolean,
) {
    private val cleanHtml: String by lazy {
        val doc = Jsoup.parse(html, url)
        doc.outputSettings().prettyPrint(false)

        if (preText) {
            // 移除代码块
            doc.select("pre,code,blockquote,table,img,iframe,hr").remove()
            // 移除导航、空标签、脚本样式
            doc.select("script,style,noscript,p:empty,div:empty").remove()
            // 清理空白元素
            doc.allElements.forEach {
                if (it.text().trim().isEmpty() && it.childrenSize() == 0) it.remove()
            }
        }

        var html = doc.html()

        html = html.replace("""\\n""", "")
        html = html.replace("""\n""", "")
        html = html.replace("""\\r""", "")
        html = html.replace("""\r""", "")

        html
    }

    private val readability: Readability4JExtended by lazy {
        Readability4JExtended(url, cleanHtml)
    }
    private val article: Article by lazy {
        readability.parse()
    }

    private val mdConverter: FlexmarkHtmlConverter by lazy {
        val options = MutableDataSet()
        FlexmarkHtmlConverter.builder(options).build()
    }

    fun toReadabilityHtml(): String {
        var contentHtml = article.contentWithUtf8Encoding ?: cleanHtml
        if (preText) {
            contentHtml = contentHtml.replace(Regex("<a.+?>(.+?)</a>"), "$1")
        }
        return contentHtml;
    }

    /**
     * 网页源码提取正文并转为 Markdown
     */
    fun toMarkdown(): String {
        val contentHtml = toReadabilityHtml()
        var md = mdConverter.convert(contentHtml)

        md = md.replace("""\\n""", "")
        md = md.replace("""\n""", "")
        md = md.replace("""\\r""", "")
        md = md.replace("""\r""", "")

        if (preText) {
            // 清除markdown标记：# * - > ` 等朗读无用符号
            md = md.replace(Regex("^\\s*#{1,6}\\s*"), "")
            md = md.replace(Regex("^\\s*[*\\-]\\s*"), "")
            md = md.replace(Regex("`{1,3}[\\s\\S]*?`{1,3}"), "")
            md = md.replace(Regex("\\[.+?]\\(.+?\\)"), "")
            md = md.replace(Regex("!\\[.*?]\\(.*?\\)"), "")
            // 统一清理换行与空格
            md = md.replace("\r", "")
            md = md.replace(Regex("\\n+"), "\n")
            md = md.lines().joinToString(" ") { it.trim() }
            md = md.replace(Regex("\\s+"), " ")
            md = md.trim()
        }

        return md
    }
}
