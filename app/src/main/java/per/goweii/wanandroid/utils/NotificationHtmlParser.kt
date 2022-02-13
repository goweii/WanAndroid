package per.goweii.wanandroid.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import per.goweii.wanandroid.module.mine.model.NotificationBean

/**
 * @author CuiZhen
 * @date 2020/5/17
 */
class NotificationHtmlParser(
        private val urlPath: String // message/lg/list/ | message/lg/history/list/
) {

    private val baseUrl = "https://www.wanandroid.com/"

    suspend fun get(page: Int): List<NotificationBean> {
        return try {
            parseDocument(request("$baseUrl$urlPath$page"))
        } catch (e: Throwable) {
            arrayListOf()
        }
    }

    suspend fun request(url: String): Document = withContext(Dispatchers.IO) {
        Jsoup.connect(url).cookies(getCookies(url)).get()
    }

    private suspend fun parseDocument(doc: Document): List<NotificationBean> = withContext(Dispatchers.IO) {
        val list = arrayListOf<NotificationBean>()
        val listArticleElements = doc.getElementsByClass("list_article list_msg")
        if (listArticleElements.isNullOrEmpty()) {
            return@withContext list
        }
        val listArticleElement = listArticleElements[0]
        val infoArtElements = listArticleElement.getElementsByClass("info_art")
        if (infoArtElements.isNullOrEmpty()) {
            return@withContext list
        }
        infoArtElements.forEach { infoArtElement ->
            val infoArtDivs = infoArtElement.children()
            val infoArtDiv1 = infoArtDivs[0]
            val tagsElements = infoArtDiv1.getElementsByClass("tags")
            val tagsElement = tagsElements[0]
            val tags = mutableListOf<String>()
            tagsElement.getElementsByTag("a").forEach {
                tags.add(it.ownText())
            }
            val aniceDate = infoArtDiv1.getElementsByClass("aniceDate")[0].ownText()
            val fromUser = infoArtDiv1.getElementsByTag("em")[0].ownText().removePrefix("@")
            val articleContent = infoArtDiv1.getElementsByTag("span")[2].ownText()
            val deleteUrlElement = infoArtDiv1.getElementsByTag("a")[0]
            var deleteUrl = deleteUrlElement.attr("href")
            if (!deleteUrl.startsWith("http://") && !deleteUrl.startsWith("https://")) {
                deleteUrl = "https://www.wanandroid.com$deleteUrl"
            }
            val infoArtDiv2 = infoArtDivs[1]
            val ariticleUrlElement = infoArtDiv2.getElementsByTag("a")[0]
            val content = ariticleUrlElement.ownText()
            var ariticleUrl = ariticleUrlElement.attr("href")
            if (!ariticleUrl.startsWith("http://") && !ariticleUrl.startsWith("https://")) {
                ariticleUrl = "https://www.wanandroid.com$ariticleUrl"
            }
            list.add(NotificationBean(tags, aniceDate, fromUser, articleContent, content, ariticleUrl, deleteUrl))
        }
        list
    }

    private fun getCookies(url: String): Map<String, String> {
        val map: MutableMap<String, String> = mutableMapOf()
        val cookies = CookieUtils.loadForUrl(url)
        if (cookies.isNullOrEmpty()) {
            return map
        }
        for (cookie in cookies) {
            map[cookie.name()] = cookie.value()
        }
        return map
    }


}