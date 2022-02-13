package per.goweii.wanandroid.module.mine.presenter

import com.google.gson.stream.MalformedJsonException
import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.http.WanApi
import per.goweii.wanandroid.module.main.model.ListBean
import per.goweii.wanandroid.module.mine.model.MessageBean
import per.goweii.wanandroid.module.mine.model.MineRequest
import per.goweii.wanandroid.module.mine.view.MessageReadedView

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
class MessageReadedPresenter : BasePresenter<MessageReadedView>() {

    fun getMessageReadList(page: Int) {
        addToRxLife(MineRequest.getMessageReadList(page, object : RequestListener<ListBean<MessageBean>> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: ListBean<MessageBean>) {
                if (isAttach) {
                    baseView.getMessageReadListSuccess(code, data)
                }
            }

            override fun onFailed(code: Int, msg: String) {
                if (isAttach) {
                    baseView.getMessageReadListFail(code, msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }

    fun delete(item: MessageBean) {
        addToRxLife(MineRequest.deleteMessage(item.id, object : RequestListener<Any?> {
            override fun onStart() {}
            override fun onSuccess(code: Int, data: Any?) {
                if (isAttach) {
                    baseView.deleteMessageSuccess(code, item)
                }
            }

            override fun onFailed(code: Int, msg: String) {
                if (isAttach) {
                    baseView.deleteMessageFail(code, msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {
                if (handle.exception is MalformedJsonException) {
                    if (isAttach) {
                        baseView.deleteMessageSuccess(WanApi.ApiCode.SUCCESS, item)
                    }
                }
            }

            override fun onFinish() {}
        }))
    }

}