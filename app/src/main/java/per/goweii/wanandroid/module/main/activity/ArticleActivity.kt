package per.goweii.wanandroid.module.main.activity

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.TextView
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener
import kotlinx.android.synthetic.main.activity_article.*
import per.goweii.basic.core.base.BaseActivity
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.UrlOpenUtils
import per.goweii.wanandroid.utils.WebHolder
import per.goweii.wanandroid.utils.WebHolder.with

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticleActivity : BaseActivity<ArticlePresenter>(), ArticleView {

    companion object {
        fun start(context: Context, url: String, title: String,
                  articleId: Int, collected: Boolean) {
            context.startActivity(Intent(context, ArticleActivity::class.java).apply {
                putExtra("url", url)
                putExtra("title", title)
                putExtra("articleId", articleId)
                putExtra("collected", collected)
            })
        }
    }

    private lateinit var mWebHolder: WebHolder
    private var url: String = ""
    private var title: String = ""
    private var articleId: Int = 0
    private var collected: Boolean = false

    override fun getLayoutId(): Int = R.layout.activity_article

    override fun initPresenter(): ArticlePresenter = ArticlePresenter()

    override fun initView() {
        intent?.let {
            url = it.getStringExtra("url")
        }
        fl_top_bar_handle.setOnClickListener {
            dl.toggle()
        }
        srl.setOnMultiPurposeListener(object : OnMultiPurposeListener {
            override fun onFooterMoving(footer: RefreshFooter?, isDragging: Boolean, percent: Float, offset: Int, footerHeight: Int, maxDragHeight: Int) {
                if (percent > 1f && dl.isClose()) {
                    dl.open()
                }
            }

            override fun onHeaderStartAnimator(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
            }

            override fun onFooterReleased(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {
            }

            override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
            }

            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
            }

            override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {
            }

            override fun onFooterStartAnimator(footer: RefreshFooter?, footerHeight: Int, maxDragHeight: Int) {
            }

            override fun onHeaderReleased(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                dl.open()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
            }

            override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
            }
        })
        dl.onDragging { v_mask.alpha = 1F - it }
        mWebHolder = with(this, wc)
                .setOnPageTitleCallback {
                    ab.getView<TextView>(R.id.tv_title).text = it
                }
                .setOverrideUrlInterceptor {
                    UrlOpenUtils.with(it).open(context)
                    return@setOverrideUrlInterceptor true
                }
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