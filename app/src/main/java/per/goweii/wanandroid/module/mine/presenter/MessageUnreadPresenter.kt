package per.goweii.wanandroid.module.mine.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.main.model.ListBean
import per.goweii.wanandroid.module.mine.model.MessageBean
import per.goweii.wanandroid.module.mine.model.MineRequest
import per.goweii.wanandroid.module.mine.view.MessageUnreadView

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
class MessageUnreadPresenter : BasePresenter<MessageUnreadView>(), CoroutineScope by MainScope() {

    fun getMessageUnreadList(page: Int) {
        addToRxLife(MineRequest.getMessageUnreadList(page, object : RequestListener<ListBean<MessageBean>> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: ListBean<MessageBean>) {
                if (isAttach) {
                    baseView.getMessageUnreadListSuccess(code, data)
                }
            }

            override fun onFailed(code: Int, msg: String) {
                if (isAttach) {
                    baseView.getMessageUnreadListFail(code, msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }

}