package per.goweii.wanandroid.module.main.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import per.goweii.rxhttp.request.base.BaseBean
import per.goweii.wanandroid.R

/**
 * @author CuiZhen
 * @date 2020/2/25
 */
class ArticleCommentAdapter : BaseQuickAdapter<BaseBean, BaseViewHolder>(R.layout.rv_item_comment) {
    override fun convert(helper: BaseViewHolder, item: BaseBean) {
        helper.run {
        }
    }
}