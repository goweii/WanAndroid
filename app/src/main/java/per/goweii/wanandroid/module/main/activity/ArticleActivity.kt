package per.goweii.wanandroid.module.main.activity

import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_article.*
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.dialog.WebGuideDialog
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.GuideSPUtils
import per.goweii.wanandroid.utils.NightModeUtils
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
    private var lastUrlLoadTime = 0L
    private var userTouched = false
    private var isPageLoadFinished = false
    private var mWebGuideDialog: WebGuideDialog? = null
    private var mGestureDetector: GestureDetector? = null

    override fun getLayoutId(): Int = R.layout.activity_article

    override fun initPresenter(): ArticlePresenter = ArticlePresenter()

    override fun initView() {
        StatusBarCompat.setIconMode(this, !NightModeUtils.isNightMode(this))
        intent?.let {
            presenter.articleUrl = it.getStringExtra("url") ?: ""
            presenter.articleTitle = it.getStringExtra("title") ?: ""
            presenter.articleId = it.getIntExtra("articleId", 0)
            presenter.collected = it.getBooleanExtra("collected", false)
            presenter.userName = it.getStringExtra("user_name") ?: ""
            presenter.userId = it.getIntExtra("user_id", 0)
        }
        switchCollectView(false)
        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                finish()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                changeRevealLayoutCenterXY(e.x, e.y)
                if (!rl.isChecked) {
                    presenter.collect()
                } else {
                    presenter.uncollect()
                }
            }
        })
        v_back.setOnTouchListener { _, event ->
            mGestureDetector?.onTouchEvent(event)
            return@setOnTouchListener true
        }
        mWebHolder = with(this, wc, pb).setOverrideUrlInterceptor {
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
        }.setOnPageLoadCallback(object : WebHolder.OnPageLoadCallback {
            override fun onPageFinished() {
                isPageLoadFinished = true
                if (!GuideSPUtils.getInstance().isArticleGuideShown) {
                    if (mWebGuideDialog == null) {
                        mWebGuideDialog = WebGuideDialog.show(context, true) {
                            GuideSPUtils.getInstance().setArticleGuideShown()
                            mWebGuideDialog = null
                        }
                    }
                }
            }

            override fun onPageStarted() {
            }
        }).setInterceptUrlInterceptor { uri, reqHeaders, reqMethod ->
            return@setInterceptUrlInterceptor WebUrlInterceptFactory.create(uri)?.interceptor?.intercept(uri, mWebHolder.userAgent, reqHeaders, reqMethod)
        }.setOnPageTitleCallback {
            presenter.readRecord(mWebHolder.url, mWebHolder.title)
        }
        wc.setOnDoubleClickListener { _, _ ->
            if (rl != null) {
                changeRevealLayoutCenterXY(rl.width * 0.5F, rl.height * 0.5F)
            }
            presenter.collect()
        }
    }

    private fun changeRevealLayoutCenterXY(x: Float, y: Float) {
        try {
            val cls = rl.javaClass
            val mCenterX = cls.getDeclaredField("mCenterX")
            val mCenterY = cls.getDeclaredField("mCenterY")
            mCenterX.isAccessible = true
            mCenterY.isAccessible = true
            mCenterX.setFloat(rl, x)
            mCenterY.setFloat(rl, y)
        } catch (e: Exception) {
        }
    }

    override fun loadData() {
        lastUrlLoadTime = System.currentTimeMillis()
        mWebHolder.loadUrl(presenter.articleUrl)
    }

    override fun onPause() {
        mWebHolder.onPause()
        super.onPause()
    }

    override fun onResume() {
        mWebHolder.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mWebHolder.onDestroy()
        super.onDestroy()
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        userTouched = true
        return super.dispatchTouchEvent(ev)
    }

    private fun switchCollectView(anim: Boolean = true) {
        rl.setChecked(presenter.collected, anim)
    }

    override fun collectSuccess() {
        switchCollectView()
    }

    override fun collectFailed(msg: String) {
        switchCollectView()
        ToastMaker.showShort(msg)
    }

    override fun uncollectSuccess() {
        switchCollectView()
    }

    override fun uncollectFailed(msg: String) {
        switchCollectView()
        ToastMaker.showShort(msg)
    }
}