package per.goweii.wanandroid.module.main.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.model.CmsCommentResp
import per.goweii.wanandroid.module.main.model.CommentItemEntity
import per.goweii.wanandroid.utils.ImageLoader
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
class ArticleCommentAdapter : BaseMultiItemQuickAdapter<CommentItemEntity, BaseViewHolder>(null) {

    @SuppressLint("SimpleDateFormat")
    private val sdfFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    @SuppressLint("SimpleDateFormat")
    private val sdfTo = SimpleDateFormat("yyyy-MM-dd HH:mm")

    private val calendar = Calendar.getInstance()

    init {
        addItemType(CommentItemEntity.TYPE_COMMENT_ROOT, R.layout.rv_item_comment_root)
        addItemType(CommentItemEntity.TYPE_COMMENT_CHILD, R.layout.rv_item_comment_child)
        addItemType(CommentItemEntity.TYPE_COMMENT_LOAD, R.layout.rv_item_comment_load)
    }

    override fun convert(helper: BaseViewHolder, item: CommentItemEntity) {
        when (helper.itemViewType) {
            CommentItemEntity.TYPE_COMMENT_ROOT -> {
                bindComment(helper, item.comment!!)
            }
            CommentItemEntity.TYPE_COMMENT_CHILD -> {
                bindComment(helper, item.comment!!)
            }
            CommentItemEntity.TYPE_COMMENT_LOAD -> {
                if (item.loading) {
                    helper.setText(R.id.tv_load, "正在加载回复")
                } else {
                    if (item.hasMore) {
                        helper.setText(R.id.tv_load, "查看更多回复")
                    } else {
                        helper.setText(R.id.tv_load, "没有更多回复")
                    }
                }
            }
        }
    }

    private fun bindComment(helper: BaseViewHolder, item: CmsCommentResp) {
        helper.run {
            setText(R.id.tv_user_name, item.user?.username ?: "已删除")
            setText(R.id.tv_content, item.content)
            setText(R.id.tv_time, item.updatedAt.formatTime())
            val civ_user_icon = getView<CircleImageView>(R.id.civ_user_icon)
            ImageLoader.userIcon(civ_user_icon, item.user?.avatar ?: "")
            item.userReply?.let { userReply ->
                setText(R.id.tv_user_name_reply, "@${userReply.username}")
            } ?: run {
                setText(R.id.tv_user_name_reply, "")
            }
        }
    }

    private fun String.formatTime(): String {
        try {
            val date = sdfFrom.parse(this)
            val commentMs = date.time
            val currentMs = System.currentTimeMillis()
            val offset = (currentMs - commentMs)
            val week = offset / (7 * 24 * 60 * 60 * 1000)
            if (week > 0) return sdfTo.format(date)
            val day = offset / (24 * 60 * 60 * 1000)
            if (day > 0) return "${day}天前"
            val hour = offset / (60 * 60 * 1000)
            if (hour > 0) return "${hour}小时前"
            val min = offset / (60 * 1000)
            if (min > 0) return "${min}分钟前"
            val sec = offset / (1000)
            if (sec > 10) return "${sec}秒前"
            return "刚刚"
        } catch (e: Exception) {
            return this
        }
    }
}