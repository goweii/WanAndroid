package per.goweii.wanandroid.module.explore

import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.http.RequestListener

class ExplorePresenter : BasePresenter<ExploreView>() {
    fun getDailyNewsFromCache() {
        DailyNewsRequest.getDailyNewsFromCache(object : RequestListener<List<DailyNewsBean>> {
            override fun onStart() {
            }

            override fun onSuccess(code: Int, data: List<DailyNewsBean>) {
                if (isAttach) {
                    baseView.getDailyNewsSuccess(data)
                }
            }

            override fun onFailed(code: Int, msg: String?) {
                if (isAttach) {
                    baseView.getDailyFailed()
                }
            }

            override fun onError(handle: ExceptionHandle?) {
            }

            override fun onFinish() {
            }
        })
    }

    fun getDailyNewsFromNet() {
        DailyNewsRequest.getDailyNewsFromNet(rxLife, object : RequestListener<List<DailyNewsBean>> {
            override fun onStart() {
            }

            override fun onSuccess(code: Int, data: List<DailyNewsBean>) {
                if (isAttach) {
                    baseView.getDailyNewsSuccess(data)
                }
            }

            override fun onFailed(code: Int, msg: String?) {
                if (isAttach) {
                    baseView.getDailyFailed()
                }
            }

            override fun onError(handle: ExceptionHandle?) {
            }

            override fun onFinish() {
            }
        })
    }
}
