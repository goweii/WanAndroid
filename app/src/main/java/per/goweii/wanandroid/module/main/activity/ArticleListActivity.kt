package per.goweii.wanandroid.module.main.activity

import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.model.ChapterBean

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
class ArticleListActivity : BaseActivity<BasePresenter<BaseView>>() {

    companion object {
        @JvmStatic
        fun start(chapterBean: ChapterBean) {

        }
    }

    override fun getLayoutId(): Int = R.layout.activity_article_list

    override fun initPresenter(): BasePresenter<BaseView>? = null

    override fun initView() {
    }

    override fun loadData() {
    }
}