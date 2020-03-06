package per.goweii.wanandroid.module.main.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener
import kotlinx.android.synthetic.main.action_bar_article.*
import kotlinx.android.synthetic.main.activity_article.*
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.utils.InputMethodUtils
import per.goweii.basic.utils.SoftInputHelper
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.home.activity.UserPageActivity
import per.goweii.wanandroid.module.main.adapter.ArticleCommentAdapter
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.UrlOpenUtils
import per.goweii.wanandroid.utils.web.WebHolder
import per.goweii.wanandroid.utils.web.WebHolder.with
import per.goweii.wanandroid.utils.web.interceptor.WebUrlInterceptFactory

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticleActivity : BaseActivity<ArticlePresenter>(), ArticleView {

    companion object {
        fun start(context: Context, url: String, title: String,
                  articleId: Int, collected: Boolean,
                  userName: String, userId: Int) {
            context.startActivity(Intent(context, ArticleActivity::class.java).apply {
                putExtra("url", url)
                putExtra("title", title)
                putExtra("articleId", articleId)
                putExtra("collected", collected)
                putExtra("user_name", userName)
                putExtra("user_id", userId)
            })
        }
    }

    private lateinit var mWebHolder: WebHolder
    private lateinit var adapter: ArticleCommentAdapter
    private var lastUrlLoadTime = 0L
    private var userTouched = false
    private var isPageLoadFinished = false

    override fun getLayoutId(): Int = R.layout.activity_article

    override fun initPresenter(): ArticlePresenter = ArticlePresenter()

    override fun initView() {
        intent?.let {
            presenter.articleUrl = it.getStringExtra("url") ?: ""
            presenter.articleTitle = it.getStringExtra("title") ?: ""
            presenter.articleId = it.getIntExtra("articleId", 0)
            presenter.collected = it.getBooleanExtra("collected", false)
            presenter.userName = it.getStringExtra("user_name") ?: ""
            presenter.userId = it.getIntExtra("user_id", 0)
        }
        ab.getView<TextView>(R.id.tv_title).text = presenter.articleTitle
        aiv_more.setOnClickListener {
            UrlOpenUtils.with(presenter.articleUrl)
                    .title(presenter.articleTitle)
                    .articleId(presenter.articleId)
                    .collected(presenter.collected)
                    .author(presenter.userName)
                    .userId(presenter.userId)
                    .forceWeb()
                    .open(context)
        }
        fl_top_bar_handle.setOnClickListener {
            dl.toggle()
        }
        srl.setOnMultiPurposeListener(object : OnMultiPurposeListener {
            override fun onFooterMoving(footer: RefreshFooter?, isDragging: Boolean, percent: Float, offset: Int, footerHeight: Int, maxDragHeight: Int) {
                if (percent > 1f && dl.isClosed()) {
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
                .setOverrideUrlInterceptor {
                    if (!isPageLoadFinished) return@setOverrideUrlInterceptor false
                    if (!userTouched) return@setOverrideUrlInterceptor false
                    val currUrlLoadTime = System.currentTimeMillis()
                    val intercept = if (currUrlLoadTime - lastUrlLoadTime > 1000L) {
                        UrlOpenUtils.with(it).open(context)
                        true
                    } else {
                        false
                    }
                    lastUrlLoadTime = currUrlLoadTime
                    return@setOverrideUrlInterceptor intercept
                }
                .setOnPageLoadCallback(object : WebHolder.OnPageLoadCallback {
                    override fun onPageFinished() {
                        isPageLoadFinished = true
                    }

                    override fun onPageStarted() {
                    }
                })
                .setInterceptUrlInterceptor { pageUri, reqUri, reqHeaders, reqMethod ->
                    return@setInterceptUrlInterceptor WebUrlInterceptFactory.create(pageUri)?.interceptor?.intercept(reqUri, mWebHolder.userAgent, reqHeaders, reqMethod)
                }
                .setNightModeInterceptor {
                    val pageUri = Uri.parse(presenter.articleUrl)
                    val supportNight = WebUrlInterceptFactory.create(pageUri)?.interceptor?.isSupportNightMode()
                            ?: false
                    return@setNightModeInterceptor !supportNight
                }
        rv.layoutManager = LinearLayoutManager(context)
        adapter = ArticleCommentAdapter()
        rv.adapter = adapter
        adapter.setEnableLoadMore(false)
        adapter.setOnLoadMoreListener({
        }, rv)
        MultiStateUtils.setEmptyAndErrorClick(msv, SimpleListener {
            MultiStateUtils.toLoading(msv)
        })
        riv_user_icon.setOnClickListener {
            UserPageActivity.start(context, presenter.userId)
        }
        tv_user_name.setOnClickListener {
            UserPageActivity.start(context, presenter.userId)
        }
        SoftInputHelper.attach(this)
                .moveWithTranslation()
                .moveBy(ll_bottom_bar)
                .moveWith(ll_bottom_bar, et_comment)
                .listener(object : SoftInputHelper.OnSoftInputListener {
                    override fun onOpen() {
                        dl.open()
                    }

                    override fun onClose() {
                        et_comment?.clearFocus()
                    }
                })
        dl.onClosed {
            et_comment?.let { InputMethodUtils.hide(it) }
        }
    }

    override fun loadData() {
        tv_user_name.text = if (presenter.userName.isNotEmpty()) {
            presenter.userName
        } else {
            "匿名"
        }
        lastUrlLoadTime = System.currentTimeMillis()
        mWebHolder.loadUrl(presenter.articleUrl)
        MultiStateUtils.toEmpty(msv)
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (dl.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        userTouched = true
        return super.dispatchTouchEvent(ev)
    }
}