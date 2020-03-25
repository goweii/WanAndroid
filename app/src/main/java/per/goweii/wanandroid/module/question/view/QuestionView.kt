package per.goweii.wanandroid.module.question.view

import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.main.model.ArticleListBean

/**
 * @author CuiZhen
 * @date 2020/3/25
 */
interface QuestionView : BaseView {
    fun getQuestionListSuccess(code: Int, data: ArticleListBean)
    fun getQuestionListFail(code: Int, msg: String)
}