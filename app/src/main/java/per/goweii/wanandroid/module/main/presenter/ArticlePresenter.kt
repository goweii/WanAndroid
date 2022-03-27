package per.goweii.wanandroid.module.main.presenter

import android.text.TextUtils
import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.base.BaseBean
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.db.executor.ReadLaterExecutor
import per.goweii.wanandroid.db.executor.ReadRecordExecutor
import per.goweii.wanandroid.event.CollectionEvent
import per.goweii.wanandroid.event.ReadRecordAddedEvent
import per.goweii.wanandroid.event.ReadRecordUpdateEvent
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.main.model.MainRequest
import per.goweii.wanandroid.module.main.view.ArticleView

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticlePresenter : BasePresenter<ArticleView>() {
    var articleUrl: String = ""
    var articleTitle: String = ""
    var articleId: Int = 0
    var readLater: Boolean = false
    var collected: Boolean = false
    var userName: String = ""
    var userId: Int = 0

    private var mReadLaterExecutor: ReadLaterExecutor? = null
    private var mReadRecordExecutor: ReadRecordExecutor? = null

    override fun attach(baseView: ArticleView) {
        super.attach(baseView)
        mReadLaterExecutor = ReadLaterExecutor()
        mReadRecordExecutor = ReadRecordExecutor()
    }

    override fun detach() {
        mReadLaterExecutor?.destroy()
        mReadRecordExecutor?.destroy()
        super.detach()
    }

    fun collect() {
        addToRxLife(MainRequest.collectArticle(articleId, object : RequestListener<BaseBean?> {
            override fun onStart() {}

            override fun onSuccess(code: Int, data: BaseBean?) {
                collected = true
                if (isAttach) {
                    baseView.collectSuccess()
                }
                CollectionEvent.postCollectWithArticleId(articleId)
            }

            override fun onFailed(code: Int, msg: String) {
                if (isAttach) {
                    baseView.collectFailed(msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }

    fun uncollect() {
        addToRxLife(MainRequest.uncollectArticle(articleId, object : RequestListener<BaseBean?> {
            override fun onStart() {}

            override fun onSuccess(code: Int, data: BaseBean?) {
                collected = false
                if (isAttach) {
                    baseView.uncollectSuccess()
                }
                CollectionEvent.postUnCollectWithArticleId(articleId)
            }

            override fun onFailed(code: Int, msg: String) {
                if (isAttach) {
                    baseView.uncollectFailed(msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }

    fun addReadRecord(link: String, title: String, percent: Float) {
        mReadRecordExecutor ?: return
        if (TextUtils.isEmpty(link)) return
        if (TextUtils.isEmpty(title)) return
        if (TextUtils.equals(link, title)) return
        mReadRecordExecutor?.add(link, title, percent, {
            ReadRecordAddedEvent(it).post()
        }, { })
    }

    fun updateReadRecordPercent(link: String, percent: Float) {
        mReadRecordExecutor ?: return
        if (TextUtils.isEmpty(link)) return
        val lastTime = System.currentTimeMillis()
        mReadRecordExecutor?.updatePercent(link, percent, lastTime, {
            val readRecordUpdateEvent = ReadRecordUpdateEvent(it.link)
            readRecordUpdateEvent.time = it.lastTime
            readRecordUpdateEvent.percent = it.percentFloat
            readRecordUpdateEvent.post()
        }, {})
    }

    fun addReadLater() {
        mReadLaterExecutor?.add(articleUrl, articleTitle, {
            readLater = true
            if (isAttach) {
                baseView.addReadLaterSuccess()
            }
        }, {
            if (isAttach) {
                baseView.addReadLaterFailed()
            }
        })
    }

    fun removeReadLater() {
        mReadLaterExecutor?.remove(articleUrl, {
            readLater = false
            if (isAttach) {
                baseView.removeReadLaterSuccess()
            }
        }, {
            if (isAttach) {
                baseView.removeReadLaterFailed()
            }
        })
    }

    fun isReadLater(callback: (Boolean) -> Unit = {}) {
        mReadLaterExecutor?.findByLink(articleUrl, {
            if (it.isNotEmpty()) {
                readLater = true
                callback.invoke(true)
            } else {
                readLater = false
                callback.invoke(false)
            }
        }, {
            readLater = false
            callback.invoke(false)
        })
    }
}