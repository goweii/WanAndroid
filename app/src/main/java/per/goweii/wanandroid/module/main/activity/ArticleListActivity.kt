package per.goweii.wanandroid.module.main.activity

import android.view.LayoutInflater
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.ActivityArticleListBinding
import per.goweii.wanandroid.module.main.model.ChapterBean

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
class ArticleListActivity : BaseActivity<BasePresenter<BaseView>, ActivityArticleListBinding>() {

    companion object {
        @JvmStatic
        fun start(chapterBean: ChapterBean) {

        }
    }

    override fun initViewBinding(inflater: LayoutInflater): ActivityArticleListBinding {
        return ActivityArticleListBinding.inflate(inflater)
    }

    override fun initPresenter(): BasePresenter<BaseView>? = null

    override fun initView() {
    }

    override fun loadData() {
    }
}