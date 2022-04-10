package per.goweii.wanandroid.module.book.contract

import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.book.model.BookBean
import per.goweii.wanandroid.module.book.model.BookRequest

interface BookView : BaseView {
    fun getBookListSuccess(list: List<BookBean>)
    fun getBookListFailed()
}

class BookPresenter : BasePresenter<BookView>() {
    fun getList() {
        BookRequest.getBookList(rxLife, object : RequestListener<List<BookBean>> {
            override fun onStart() {
            }

            override fun onSuccess(code: Int, data: List<BookBean>) {
                if (isAttach) {
                    baseView.getBookListSuccess(data)
                }
            }

            override fun onFailed(code: Int, msg: String?) {
                if (isAttach) {
                    baseView.getBookListFailed()
                }
            }

            override fun onError(handle: ExceptionHandle?) {
            }

            override fun onFinish() {
            }
        })
    }
}