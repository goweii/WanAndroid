package per.goweii.wanandroid.module.explore

import per.goweii.basic.core.base.BaseView

interface ExploreView : BaseView {
    fun getDailyNewsSuccess(platform: DailyNewsPlatform, data: List<DailyNewsBean>)
    fun getDailyFailed(platform: DailyNewsPlatform)
}