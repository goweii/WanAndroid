package per.goweii.wanandroid.module.book.contract

import kotlinx.coroutines.*
import okhttp3.Cookie
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.book.bean.BookBean
import per.goweii.wanandroid.utils.CookieUtils.loadForUrl

interface BookView : BaseView {
    fun getBookListSuccess(list: List<BookBean>)
    fun getBookListFailed()
}

class BookPresenter : BasePresenter<BookView>() {
    private lateinit var mainScope: CoroutineScope

    override fun attach(baseView: BookView?) {
        super.attach(baseView)
        mainScope = MainScope()
    }

    override fun detach() {
        super.detach()
        mainScope.cancel()
    }

    fun getList() = mainScope.launch {
        try {
            val list = getListByJSoup()
            if (isAttach) {
                baseView.getBookListSuccess(list)
            }
        } catch (e: Throwable) {
            if (isAttach) {
                baseView.getBookListFailed()
            }
        }
    }

    private suspend fun getListByJSoup(): List<BookBean> = withContext(Dispatchers.IO) {
        val url = "https://www.wanandroid.com/book/list"
        val cookies: List<Cookie> = loadForUrl(url)
        val map: MutableMap<String, String> = HashMap(cookies.size)
        for (cookie in cookies) {
            map[cookie.name()] = cookie.value()
        }
        val document = Jsoup.connect(url).cookies(map).get()
        val list = arrayListOf<BookBean>()
        document.getElementsByClass("main_content_l").first()
            .children().asSequence()
            .filter { it.tagName() == "div" }
            .forEach {
                try {
                    list.add(parseBookBean(it))
                } catch (unused: Throwable) {
                    unused.printStackTrace()
                }
            }
        return@withContext list
    }

    private fun parseBookBean(element: Element): BookBean {
        var img: String?
        var link: String?
        var author: String?
        var name: String?
        var desc: String?
        element.getElementsByClass("book_img").first()
            .getElementsByTag("a").first()
            .apply {
                img = getElementsByTag("img")
                    .first()
                    .attributes()
                    .get("src")
            }
            .apply {
                link = attributes().get("href")
            }
        element.getElementsByClass("book_info").first()
            .apply {
                name = getElementsByClass("text_ofh").first()
                    .getElementsByTag("a").first()
                    .ownText()
            }
            .getElementsByTag("p")
            .apply {
                author = this[0].ownText().removePrefix("作者：")
                desc = this[1].ownText()
            }
        return BookBean(
            img = img!!,
            author = author!!,
            name = name!!,
            desc = desc!!,
            link = "https://www.wanandroid.com${link!!}",
        )
    }
}