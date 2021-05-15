package per.goweii.wanandroid.module.main.view

import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.main.model.CmsCommentResp
import per.goweii.wanandroid.module.main.model.CommentItemEntity

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
interface ArticleView : BaseView {
    fun collectSuccess()
    fun collectFailed(msg: String)
    fun uncollectSuccess()
    fun uncollectFailed(msg: String)
    fun addReadLaterSuccess()
    fun addReadLaterFailed()
    fun removeReadLaterSuccess()
    fun removeReadLaterFailed()
    fun commentCountSuccess(resp: Int)
    fun commentCountFailed(msg: String)
    fun commentSuccess(resp: CmsCommentResp)
    fun commentFailed(msg: String)
    fun commentsSuccess(resp: List<CmsCommentResp>)
    fun commentsFailed(msg: String)
    fun commentsReplysSuccess(item: CommentItemEntity, resp: List<CmsCommentResp>)
    fun commentsReplysFailed(item: CommentItemEntity)
}