package per.goweii.wanandroid.module.mine.adapter

import android.text.Html
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import per.goweii.basic.utils.StringUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.mine.model.MessageBean

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class MessageUnreadAdapter : BaseQuickAdapter<MessageBean, BaseViewHolder>(R.layout.rv_item_message_unread) {
    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        if (TextUtils.isEmpty(item.tag)) {
            helper.setGone(R.id.tv_tag, false)
        } else {
            helper.setGone(R.id.tv_tag, true)
            helper.setText(R.id.tv_tag, item.tag)
        }
        helper.setText(R.id.tv_user, item.fromUser)
        helper.setText(R.id.tv_data, item.niceDate)
        helper.setText(R.id.tv_detail, item.title)
        var content: String = Html.fromHtml(item.message).toString()
        content = StringUtils.removeAllBank(content, 2)
        helper.setGone(R.id.tv_content, content.isNotEmpty())
        helper.setText(R.id.tv_content, content)
    }
}