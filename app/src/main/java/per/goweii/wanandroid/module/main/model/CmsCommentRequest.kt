package per.goweii.wanandroid.module.main.model

import io.reactivex.disposables.Disposable
import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.http.CmsBaseRequest
import per.goweii.wanandroid.utils.UserUtils

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
object CmsCommentRequest : CmsBaseRequest() {
    fun comment(url: String,
                content: String,
                commentRoot: String? = null,
                commentReply: String? = null,
                userReply: String? = null,
                listener: Listener<CmsCommentResp>
    ): Disposable {
        val cmsId = UserUtils.getInstance().cmsId
        val commentRootId = if (commentRoot.isNullOrEmpty()) null else commentRoot
        val commentReplyId = if (commentReply.isNullOrEmpty()) null else commentReply
        val userReplyId = if (userReply.isNullOrEmpty()) null else userReply
        val body = CmsCommentBody(url, cmsId, content, commentRootId, commentReplyId, userReplyId)
        return request(CmsApi.api().comment(body), listener)
    }

    fun comments(url: String,
                 offset: Int,
                 limit: Int,
                 queryMap: Map<String, String>,
                 listener: Listener<List<CmsCommentResp>>
    ): Disposable {
        return request(CmsApi.api().comments(
                articleUrl = url,
                offset = offset,
                limit = limit,
                queryMap = queryMap
        ), listener)
    }

    fun commentCount(url: String, listener: Listener<Int>): Disposable {
        return request(CmsApi.api().commentCount(url), listener)
    }
}