package per.goweii.wanandroid.module.mine.view

import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.mine.model.NotificationBean

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
interface NotificationHistoryView : BaseView {
    fun getListSuccess(list: List<NotificationBean>)
}