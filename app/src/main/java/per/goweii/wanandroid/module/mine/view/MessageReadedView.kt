package per.goweii.wanandroid.module.mine.view

import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.main.model.ListBean
import per.goweii.wanandroid.module.mine.model.MessageBean

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
interface MessageReadedView : BaseView {
    fun getMessageReadListSuccess(code: Int, data: ListBean<MessageBean>)
    fun getMessageReadListFail(code: Int, msg: String)
    fun deleteMessageSuccess(code: Int, data: MessageBean)
    fun deleteMessageFail(code: Int, msg: String)
}