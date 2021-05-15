package per.goweii.wanandroid.module.mine.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import per.goweii.basic.core.base.BasePresenter
import per.goweii.wanandroid.event.NotificationDeleteEvent
import per.goweii.wanandroid.module.mine.model.NotificationBean
import per.goweii.wanandroid.module.mine.view.NotificationHistoryView
import per.goweii.wanandroid.utils.NotificationHtmlParser

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
class NotificationHistoryPresenter : BasePresenter<NotificationHistoryView>(), CoroutineScope by MainScope() {

    private val notificationNewPresenter = NotificationHtmlParser("message/lg/history/list/")

    fun getList(page: Int) {
        launch {
            val list = notificationNewPresenter.get(page)
            if (isAttach) {
                baseView.getListSuccess(list)
            }
        }
    }

    fun delete(item: NotificationBean) {
        launch {
            notificationNewPresenter.request(item.deleteUrl)
            NotificationDeleteEvent.post(item)
        }
    }

}