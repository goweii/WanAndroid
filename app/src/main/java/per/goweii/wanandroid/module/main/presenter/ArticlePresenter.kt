package per.goweii.wanandroid.module.main.presenter

import android.text.TextUtils
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.rxhttp.request.base.BaseBean
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.db.executor.ReadRecordExecutor
import per.goweii.wanandroid.event.CollectionEvent
import per.goweii.wanandroid.event.ReadRecordEvent
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.main.model.MainRequest
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.SettingUtils

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticlePresenter : BasePresenter<ArticleView>() {
    var articleUrl: String = ""
    var articleTitle: String = ""
    var articleId: Int = 0
    var collected: Boolean = false
    var userName: String = ""
    var userId: Int = 0

    private var mReadRecordExecutor: ReadRecordExecutor? = null

    override fun attach(baseView: ArticleView) {
        super.attach(baseView)
        mReadRecordExecutor = ReadRecordExecutor()
    }

    override fun detach() {
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

    fun readRecord(link: String, title: String) {
        mReadRecordExecutor ?: return
        if (!SettingUtils.getInstance().isShowReadRecord) {
            return
        }
        if (TextUtils.isEmpty(link)) {
            return
        }
        if (TextUtils.isEmpty(title)) {
            return
        }
        if (TextUtils.equals(link, title)) {
            return
        }
        mReadRecordExecutor?.add(link, title, SimpleListener {
            ReadRecordEvent().post()
        }, SimpleListener { })
    }
}