package per.goweii.wanandroid.module.main.presenter

import android.text.TextUtils
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.rxhttp.request.base.BaseBean
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.db.executor.ReadLaterExecutor
import per.goweii.wanandroid.db.executor.ReadRecordExecutor
import per.goweii.wanandroid.event.CollectionEvent
import per.goweii.wanandroid.event.ReadRecordEvent
import per.goweii.wanandroid.http.CmsBaseRequest
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.main.model.CmsCommentRequest
import per.goweii.wanandroid.module.main.model.CommentItemEntity
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
    var readLater: Boolean = false
    var collected: Boolean = false
    var userName: String = ""
    var userId: Int = 0

    var commentCount = 0

    var commentContent: String? = null
    var commentReply: CommentItemEntity? = null

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

    fun commentCount() {
        addToRxLife(CmsCommentRequest.commentCount(articleUrl, CmsBaseRequest.Listener(
                onSuccess = {
                    commentCount = it
                    if (isAttach) {
                        baseView.commentCountSuccess(it)
                    }
                },
                onFailure = {
                    if (isAttach) {
                        baseView.commentCountFailed(it)
                    }
                }
        )))
    }

    fun comment() {
        if (commentContent.isNullOrEmpty()) {
            ToastMaker.showShort("请输入评论")
            return
        }
        val content = commentContent!!
        val commentRootId = commentReply?.rootItem?.comment?.id
        val commentReplyId = commentReply?.comment?.id
        val userReplyId = commentReply?.comment?.user?.id
        addToRxLife(CmsCommentRequest.comment(articleUrl, content,
                commentRootId, commentReplyId, userReplyId,
                CmsBaseRequest.Listener(
                        onStart = {
                            showLoadingDialog()
                        },
                        onFinish = {
                            dismissLoadingDialog()
                        },
                        onSuccess = {
                            commentCount++
                            if (isAttach) {
                                baseView.commentCountSuccess(commentCount)
                                baseView.commentSuccess(it)
                            }
                        },
                        onFailure = {
                            if (isAttach) {
                                baseView.commentFailed(it)
                            }
                        }
                )))
    }

    fun comments(offset: Int, limit: Int) {
        val queryMap = mutableMapOf<String, String>().apply {
            put("commentRoot_null", "true")
            put("commentReply_null", "true")
        }
        addToRxLife(CmsCommentRequest.comments(articleUrl, offset, limit, queryMap, CmsBaseRequest.Listener(
                onSuccess = {
                    if (isAttach) {
                        baseView.commentsSuccess(it)
                    }
                },
                onFailure = {
                    if (isAttach) {
                        baseView.commentsFailed(it)
                    }
                }
        )))
    }

    fun commentReplys(item: CommentItemEntity, rootId: String, offset: Int, limit: Int) {
        val queryMap = mutableMapOf<String, String>().apply {
            put("commentRoot.id", rootId)
        }
        addToRxLife(CmsCommentRequest.comments(articleUrl, offset, limit, queryMap, CmsBaseRequest.Listener(
                onSuccess = {
                    if (isAttach) {
                        baseView.commentsReplysSuccess(item, it)
                    }
                },
                onFailure = {
                    if (isAttach) {
                        baseView.commentsReplysFailed(item)
                    }
                }
        )))
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
        mReadRecordExecutor?.add(link, title, {
            ReadRecordEvent().post()
        }, { })
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