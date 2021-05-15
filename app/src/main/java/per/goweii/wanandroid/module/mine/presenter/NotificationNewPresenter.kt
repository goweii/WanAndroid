package per.goweii.wanandroid.module.mine.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import per.goweii.basic.core.base.BasePresenter
import per.goweii.wanandroid.module.mine.view.NotificationNewView
import per.goweii.wanandroid.utils.NotificationHtmlParser

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
class NotificationNewPresenter : BasePresenter<NotificationNewView>(), CoroutineScope by MainScope() {

    private val notificationNewPresenter = NotificationHtmlParser("message/lg/list/")

    fun getList(page: Int) {
        val url = "https://www.wanandroid.com/message/lg/list/$page"
        launch {
            val list = notificationNewPresenter.get(page)
            if (isAttach) {
                baseView.getListSuccess(list)
            }
        }
    }

}