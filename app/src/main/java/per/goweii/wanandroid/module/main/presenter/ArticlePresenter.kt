package per.goweii.wanandroid.module.main.presenter

import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.base.BaseBean
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.event.CollectionEvent
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
    var collected: Boolean = false
    var userName: String = ""
    var userId: Int = 0

    fun collect() {
        addToRxLife(MainRequest.collect(articleId, object : RequestListener<BaseBean?> {
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
        addToRxLife(MainRequest.uncollect(articleId, object : RequestListener<BaseBean?> {
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
}