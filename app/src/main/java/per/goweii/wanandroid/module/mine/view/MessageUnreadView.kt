package per.goweii.wanandroid.module.mine.view

import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.main.model.ListBean
import per.goweii.wanandroid.module.mine.model.MessageBean

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
interface MessageUnreadView : BaseView {
    fun getMessageUnreadListSuccess(code: Int, data: ListBean<MessageBean>)
    fun getMessageUnreadListFail(code: Int, msg: String)
}