package per.goweii.wanandroid.module.main.model

/**
 * @author CuiZhen
 * @date 2020/5/24
 */
data class CmsCommentBody(
        val articleUrl: String,
        val user: String,
        val content: String,
        val commentRoot: String? = null,
        val commentReply: String? = null,
        val userReply: String? = null
)