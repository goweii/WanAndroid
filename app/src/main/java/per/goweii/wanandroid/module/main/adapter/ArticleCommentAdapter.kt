package per.goweii.wanandroid.module.main.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sohu.cyan.android.sdk.entity.Comment
import per.goweii.wanandroid.R

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
class ArticleCommentAdapter : BaseQuickAdapter<Comment, BaseViewHolder>(R.layout.rv_item_comment) {
    override fun convert(helper: BaseViewHolder, item: Comment) {
        helper.run {
            setText(R.id.tv_user_name, item.from)
            setText(R.id.tv_time, item.create_time.toString())
            setText(R.id.tv_content, item.content)
        }
    }
}