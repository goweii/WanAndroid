package per.goweii.wanandroid.module.main.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * @author CuiZhen
 * @date 2020/5/31
 */
data class CommentItemEntity(
        val comment: CmsCommentResp?,
        val commentRoot: String?,
        var hasMore: Boolean,
        var loading: Boolean,
        var offset: Int,
        val limit: Int,

        var rootItem: CommentItemEntity? = null,
        var loadItem: CommentItemEntity? = null
) : MultiItemEntity {

    companion object {
        const val TYPE_COMMENT_ROOT = 1
        const val TYPE_COMMENT_CHILD = 2
        const val TYPE_COMMENT_LOAD = 3

        fun createTypeComment(comment: CmsCommentResp) = CommentItemEntity(
                comment = comment,
                commentRoot = null,
                hasMore = false,
                loading = false,
                offset = 0,
                limit = 0
        )

        fun createTypeLoad(commentRoot: String) = CommentItemEntity(
                comment = null,
                commentRoot = commentRoot,
                hasMore = true,
                loading = false,
                offset = 0,
                limit = 3
        )
    }

    override fun getItemType(): Int {
        return if (comment != null) {
            if (comment.commentRoot == null) {
                TYPE_COMMENT_ROOT
            } else {
                TYPE_COMMENT_CHILD
            }
        } else {
            TYPE_COMMENT_LOAD
        }
    }
}