package per.goweii.wanandroid.module.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import per.goweii.basic.core.base.BaseFragment
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.FragmentArticleListBinding
import per.goweii.wanandroid.module.main.contract.ArticleListPresenter
import per.goweii.wanandroid.module.main.contract.ArticleListView

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
class ArticleListFragment : BaseFragment<ArticleListPresenter, FragmentArticleListBinding>(), ArticleListView {

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentArticleListBinding {
        return FragmentArticleListBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): ArticleListPresenter = ArticleListPresenter()

    override fun initView() {
    }

    override fun loadData() {
    }
}