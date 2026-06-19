package per.goweii.wanandroid.module.explore

import per.goweii.basic.core.base.BasePresenter
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.utils.SettingUtils

class ExplorePresenter : BasePresenter<ExploreView>() {
    fun getDailyNewsFromCache() {
        val platform = SettingUtils.getInstance().dailyNewsPlatform
        DailyNewsRequest.getDailyNewsFromCache(
            platform,
            object : RequestListener<List<DailyNewsBean>> {
                override fun onStart() {
                }

                override fun onSuccess(code: Int, data: List<DailyNewsBean>) {
                    if (isAttach && SettingUtils.getInstance().dailyNewsPlatform == platform) {
                        baseView.getDailyNewsSuccess(platform, data)
                    }
                }

                override fun onFailed(code: Int, msg: String?) {
                    if (isAttach && SettingUtils.getInstance().dailyNewsPlatform == platform) {
                        baseView.getDailyFailed(platform)
                    }
                }

                override fun onError(handle: ExceptionHandle?) {
                }

                override fun onFinish() {
                }
            })
    }

    fun getDailyNewsFromNet() {
        val platform = SettingUtils.getInstance().dailyNewsPlatform
        DailyNewsRequest.getDailyNewsFromNet(
            platform,
            rxLife,
            object : RequestListener<List<DailyNewsBean>> {
                override fun onStart() {
                }

                override fun onSuccess(code: Int, data: List<DailyNewsBean>) {
                    if (isAttach && SettingUtils.getInstance().dailyNewsPlatform == platform) {
                        baseView.getDailyNewsSuccess(platform, data)
                    }
                }

                override fun onFailed(code: Int, msg: String?) {
                    if (isAttach && SettingUtils.getInstance().dailyNewsPlatform == platform) {
                        baseView.getDailyFailed(platform)
                    }
                }

                override fun onError(handle: ExceptionHandle?) {
                }

                override fun onFinish() {
                }
            })
    }
}
