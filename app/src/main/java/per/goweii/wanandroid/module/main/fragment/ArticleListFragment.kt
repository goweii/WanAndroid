package per.goweii.wanandroid.module.main.fragment

import per.goweii.basic.core.base.BaseFragment
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.contract.ArticleListPresenter
import per.goweii.wanandroid.module.main.contract.ArticleListView

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
class ArticleListFragment : BaseFragment<ArticleListPresenter>(), ArticleListView {

    override fun getLayoutRes(): Int = R.layout.fragment_article_list

    override fun initPresenter(): ArticleListPresenter = ArticleListPresenter()

    override fun initView() {
    }

    override fun loadData() {
    }
}