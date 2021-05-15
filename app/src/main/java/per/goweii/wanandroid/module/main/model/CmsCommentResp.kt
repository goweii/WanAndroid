package per.goweii.wanandroid.module.main.model

import per.goweii.wanandroid.http.CmsBaseResponse
import per.goweii.wanandroid.module.mine.model.CmsUserResp

/**
 * @author CuiZhen
 * @date 2020/5/24
 */
data class CmsCommentResp(
        val user: CmsUserResp?,
        val id: String,
        val articleUrl: String,
        val content: String,
        val commentRoot: CmsCommentChild?,
        val commentReply: CmsCommentChild?,
        val userReply: CmsUserResp?,
        val createdAt: String,
        val updatedAt: String
) : CmsBaseResponse() {
    data class CmsCommentChild(
            val user: String?,
            val id: String,
            val articleUrl: String,
            val createdAt: String,
            val updatedAt: String
    )
}