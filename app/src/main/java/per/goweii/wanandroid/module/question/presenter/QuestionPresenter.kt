package per.goweii.wanandroid.module.question.presenter

import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.base.BaseBean
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.event.CollectionEvent
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.main.model.ArticleBean
import per.goweii.wanandroid.module.main.model.ArticleListBean
import per.goweii.wanandroid.module.main.model.MainRequest
import per.goweii.wanandroid.module.question.model.QuestionRequest
import per.goweii.wanandroid.module.question.view.QuestionView
import per.goweii.wanandroid.widget.CollectView

/**
 * @author CuiZhen
 * @date 2020/3/25
 */
class QuestionPresenter : BasePresenter<QuestionView>() {

    fun getQuestionListCache(page: Int) {
        QuestionRequest.getQuestionListCache(page, object : RequestListener<ArticleListBean> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: ArticleListBean) {
                if (isAttach) {
                    baseView.getQuestionListSuccess(code, data)
                }
            }

            override fun onFailed(code: Int, msg: String) {}
            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        })
    }

    fun getQuestionList(page: Int) {
        QuestionRequest.getQuestionList(rxLife, page, object : RequestListener<ArticleListBean> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: ArticleListBean) {
                if (isAttach) {
                    baseView.getQuestionListSuccess(code, data)
                }
            }

            override fun onFailed(code: Int, msg: String) {
                if (isAttach) {
                    baseView.getQuestionListFail(code, msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        })
    }

    fun collect(item: ArticleBean, v: CollectView) {
        addToRxLife(MainRequest.collectArticle(item.id, object : RequestListener<BaseBean?> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: BaseBean?) {
                item.isCollect = true
                if (!v.isChecked) {
                    v.toggle()
                }
                CollectionEvent.postCollectWithArticleId(item.id)
            }

            override fun onFailed(code: Int, msg: String) {
                if (v.isChecked) {
                    v.toggle()
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }

    fun uncollect(item: ArticleBean, v: CollectView) {
        addToRxLife(MainRequest.uncollectArticle(item.id, object : RequestListener<BaseBean?> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: BaseBean?) {
                item.isCollect = false
                if (v.isChecked) {
                    v.toggle()
                }
                CollectionEvent.postUnCollectWithArticleId(item.id)
            }

            override fun onFailed(code: Int, msg: String) {
                if (!v.isChecked) {
                    v.toggle()
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }
}