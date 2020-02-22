package per.goweii.wanandroid.module.main.activity

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_article.*
import per.goweii.basic.core.base.BaseActivity
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.WebHolder
import per.goweii.wanandroid.utils.WebHolder.with

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticleActivity : BaseActivity<ArticlePresenter>(), ArticleView {

    companion object {
        fun startSelf(context: Context, url: String) {
            context.startActivity(Intent(context, ArticleActivity::class.java).apply {
                putExtra("params_url", url)
            })
        }
    }

    private lateinit var mWebHolder: WebHolder
    private var url: String = ""

    override fun getLayoutId(): Int = R.layout.activity_article

    override fun initPresenter(): ArticlePresenter = ArticlePresenter()

    override fun initView() {
        intent?.let {
            url = it.getStringExtra("params_url")
        }
        tv_pinglun.setOnClickListener {
            dl.toggle()
        }
        mWebHolder = with(this, wc)
    }

    override fun loadData() {
        mWebHolder.loadUrl(url)
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (dl.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }
}