package per.goweii.wanandroid.module.main.activity

import android.content.Context
import android.content.Intent
import per.goweii.basic.core.base.BaseActivity
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.view.ArticleView

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticleActivity : BaseActivity<ArticlePresenter>(), ArticleView {

    companion object {
        fun startSelf(context: Context) {
            context.startActivity(Intent(context, ArticleActivity::class.java))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_article

    override fun initPresenter(): ArticlePresenter = ArticlePresenter()

    override fun initView() {
    }

    override fun loadData() {
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }
}