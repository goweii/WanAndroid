package per.goweii.wanandroid.module.main.view

import per.goweii.basic.core.base.BaseView

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
interface ArticleView : BaseView {
    fun collectSuccess()
    fun collectFailed(msg: String)
    fun uncollectSuccess()
    fun uncollectFailed(msg: String)
}