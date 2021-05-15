package per.goweii.wanandroid.http

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import per.goweii.basic.core.utils.JsonFormatUtils.toJson
import per.goweii.basic.utils.LogUtils
import per.goweii.rxhttp.request.RxResponse
import per.goweii.rxhttp.request.exception.ExceptionHandle

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
open class CmsBaseRequest {

    class Listener<Resp>(
            val onStart: (() -> Unit)? = null,
            val onFinish: (() -> Unit)? = null,
            val onSuccess: ((resp: Resp) -> Unit)? = null,
            val onFailure: ((msg: String) -> Unit)? = null
    )

    protected fun <Resp> request(
            observable: Observable<Resp>,
            listener: Listener<Resp>? = null
    ): Disposable {
        RxResponse.create(observable)
                .listener(object : RxResponse.RequestListener {
                    override fun onStart() {
                    }

                    override fun onFinish() {
                    }
                })
        return RxResponse.create(observable)
                .listener(object : RxResponse.RequestListener {
                    override fun onStart() {
                        listener?.onStart?.invoke()
                    }

                    override fun onFinish() {
                        listener?.onFinish?.invoke()
                    }
                })
                .request(object : RxResponse.ResultCallback<Resp> {
                    override fun onResponse(resp: Resp) {
                        LogUtils.httpi(toJson(resp))
                        if (resp is CmsBaseResponse) {
                            if (resp.statusCode == 0) {
                                listener?.onSuccess?.invoke(resp)
                            } else {
                                listener?.onFailure?.invoke(resp.error)
                            }
                        } else {
                            listener?.onSuccess?.invoke(resp)
                        }
                    }

                    override fun onError(handle: ExceptionHandle) {
                        handle.exception.printStackTrace()
                        LogUtils.httpe(handle.msg)
                        listener?.onFailure?.invoke(handle.msg)
                    }
                })
    }
}