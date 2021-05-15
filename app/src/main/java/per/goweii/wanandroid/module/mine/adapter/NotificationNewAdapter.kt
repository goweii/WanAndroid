package per.goweii.wanandroid.module.mine.adapter

import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import per.goweii.basic.utils.StringUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.mine.model.NotificationBean

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class NotificationNewAdapter : BaseQuickAdapter<NotificationBean, BaseViewHolder>(R.layout.rv_item_notification) {
    override fun convert(helper: BaseViewHolder, item: NotificationBean) {
        if (item.tags.isEmpty()) {
            helper.setGone(R.id.tv_tag, false)
        } else {
            helper.setGone(R.id.tv_tag, true)
            helper.setText(R.id.tv_tag, item.tags[0])
        }
        helper.setText(R.id.tv_user, item.fromUser)
        helper.setText(R.id.tv_data, item.aniceDate)
        helper.setText(R.id.tv_detail, item.articleContent)
        var content: String = Html.fromHtml(item.content).toString()
        content = StringUtils.removeAllBank(content, 2)
        helper.setGone(R.id.tv_content, content.isNotEmpty())
        helper.setText(R.id.tv_content, content)
    }
}