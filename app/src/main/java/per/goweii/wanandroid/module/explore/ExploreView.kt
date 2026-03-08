package per.goweii.wanandroid.module.explore

import per.goweii.basic.core.base.BaseView

interface ExploreView : BaseView {
    fun getDailyNewsSuccess(data: List<DailyNewsBean>)
    fun getDailyFailed()
}