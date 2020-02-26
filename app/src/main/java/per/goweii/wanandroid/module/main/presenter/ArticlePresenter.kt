package per.goweii.wanandroid.module.main.presenter

import per.goweii.basic.core.base.BasePresenter
import per.goweii.wanandroid.module.main.view.ArticleView

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticlePresenter : BasePresenter<ArticleView>() {
    var articleUrl: String = ""
    var articleTitle: String = ""
    var articleId: Int = 0
    var collected: Boolean = false
    var userName: String = ""
    var userId: Int = 0
}