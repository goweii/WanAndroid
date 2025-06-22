package per.goweii.wanandroid.module.main.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import per.goweii.anylayer.Layer
import per.goweii.anylayer.guide.GuideLayer
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.ResUtils
import per.goweii.basic.utils.ext.invisible
import per.goweii.basic.utils.ext.visible
import per.goweii.wanandroid.utils.GoogleAdUnitIds
import per.goweii.component.ad.BannerAdProvider
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.swipeback.SwipeBackDirection
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.ActivityArticleBinding
import per.goweii.wanandroid.module.main.dialog.ArticleShareDialog
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.utils.FloatIconTouchListener
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.DarkModeUtils
import per.goweii.wanandroid.utils.GuideSPUtils
import per.goweii.wanandroid.utils.ImageLoader
import per.goweii.wanandroid.utils.RecommendManager
import per.goweii.wanandroid.utils.UrlOpenUtils
import per.goweii.wanandroid.utils.cdkey.CDKeyUtils
import per.goweii.wanandroid.utils.router.Router
import per.goweii.wanandroid.utils.web.WebHolder
import per.goweii.wanandroid.utils.web.WebHolder.with
import per.goweii.wanandroid.utils.web.cache.ReadingModeManager
import per.goweii.wanandroid.utils.web.interceptor.WebReadingModeInterceptor
import per.goweii.wanandroid.utils.web.interceptor.WebResUrlInterceptor

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticleActivity : BaseActivity<ArticlePresenter, ActivityArticleBinding>(), ArticleView,
    SwipeBackAbility.Direction, SwipeBackAbility.ForceEdge {
    private data class FloatIcon(
        val container: View,
        val shadow: View,
        val icon: View,
        val tip: View,
        var tipAnim: Animator? = null
    )

    companion object {
        fun start(
            context: Context, url: String, title: String,
            articleId: Int, collected: Boolean,
            userName: String, userId: Int
        ) {
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

    private var floatIconsVisible = false
    private var floatIconsAnim: AnimatorSet? = null
    private val floatIcons: List<FloatIcon> by lazy {
        mutableListOf<FloatIcon>().apply {
            add(
                FloatIcon(
                    binding.activityArticleFloatBtn.rlIconCollect,
                    binding.activityArticleFloatBtn.slCollect,
                    binding.activityArticleFloatBtn.cvCollect,
                    binding.activityArticleFloatBtn.tvCollectTip
                )
            )
            add(
                FloatIcon(
                    binding.activityArticleFloatBtn.rlIconReadLater,
                    binding.activityArticleFloatBtn.slReadLater,
                    binding.activityArticleFloatBtn.aivReadLater,
                    binding.activityArticleFloatBtn.tvReadLaterTip
                )
            )
            add(
                FloatIcon(
                    binding.activityArticleFloatBtn.rlIconOpen,
                    binding.activityArticleFloatBtn.slOpen,
                    binding.activityArticleFloatBtn.aivOpen,
                    binding.activityArticleFloatBtn.tvOpenTip
                )
            )
            add(
                FloatIcon(
                    binding.activityArticleFloatBtn.rlIconShare,
                    binding.activityArticleFloatBtn.slShare,
                    binding.activityArticleFloatBtn.aivShare,
                    binding.activityArticleFloatBtn.tvShareTip
                )
            )
        }
    }

    override fun initViewBinding(inflater: LayoutInflater): ActivityArticleBinding {
        return ActivityArticleBinding.inflate(inflater)
    }

    override fun initPresenter(): ArticlePresenter = ArticlePresenter()

    override fun initView() {
        StatusBarCompat.setIconMode(this, !DarkModeUtils.isDarkMode(this))
        intent?.let {
            presenter.articleUrl = it.getStringExtra("url") ?: ""
            presenter.articleTitle = it.getStringExtra("title") ?: ""
            presenter.articleId = it.getIntExtra("articleId", 0)
            presenter.collected = it.getBooleanExtra("collected", false)
            presenter.userName = it.getStringExtra("user_name") ?: ""
            presenter.userId = it.getIntExtra("user_id", 0)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val b = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            binding.root.updatePadding(bottom = b)
            insets
        }

        switchCollectView(false)
        val icons = mutableListOf<FloatIconTouchListener.Icon>()
        floatIcons.forEach {
            icons.add(FloatIconTouchListener.Icon(it.icon))
        }
        binding.activityArticleFloatBtn.vBack.setOnTouchListener(
            FloatIconTouchListener(
                icons,
                object : FloatIconTouchListener.OnFloatTouchedListener {
                    override fun onTouched(v: View?) {
                        var floatIcon: FloatIcon? = null
                        floatIcons.forEach {
                            if (it.icon == v) {
                                floatIcon = it
                            }
                        }
                        doFloatTipAnim(floatIcon)
                    }
                })
        )
        binding.activityArticleFloatBtn.vBack.setOnClickListener {
            if (floatIconsVisible) toggleFloatIcons()
            else finish()
        }
        binding.activityArticleFloatBtn.vBack.setOnLongClickListener {
            toggleFloatIcons()
            return@setOnLongClickListener true
        }
        binding.activityArticleFloatBtn.aivShare.setOnClickListener {
            shareQrcode()
            if (floatIconsVisible) toggleFloatIcons()
        }
        binding.activityArticleFloatBtn.aivReadLater.setOnClickListener {
            presenter.isReadLater { isReadLater ->
                if (isReadLater) {
                    presenter.removeReadLater()
                } else {
                    presenter.addReadLater()
                }
            }
            if (floatIconsVisible) toggleFloatIcons()
        }
        binding.activityArticleFloatBtn.aivOpen.setOnClickListener {
            UrlOpenUtils.with(presenter.articleUrl)
                .title(presenter.articleTitle)
                .articleId(presenter.articleId)
                .collected(presenter.collected)
                .author(presenter.userName)
                .userId(presenter.userId)
                .forceWeb()
                .open(context)
            if (floatIconsVisible) toggleFloatIcons()
        }
        binding.activityArticleFloatBtn.cvCollect.setOnClickListener {
            if (binding.activityArticleFloatBtn.cvCollect.isChecked) {
                presenter.collect()
            } else {
                presenter.uncollect()
            }
            if (floatIconsVisible) toggleFloatIcons()
        }
        binding.wc.setOnTouchDownListener {
            if (floatIconsVisible) toggleFloatIcons()
        }
        mWebHolder = with(this, binding.wc, binding.activityArticleFloatBtn.pb)
            .setLoadCacheElseNetwork(true)
            .setUseInstanceCache(true)
            .setAllowOpenOtherApp(false)
            .setAllowOpenDownload(false)
            .setAllowRedirect(false)
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
                override fun onPageStarted() {
                    presenter.addReadRecord(mWebHolder.url, mWebHolder.title, mWebHolder.percent)
                }

                override fun onPageFinished() {
                    isPageLoadFinished = true

                    presenter.updateReadRecordTitle(mWebHolder.url, mWebHolder.title)

                    showArticleFooter()

                    val uri = Uri.parse(mWebHolder.url)
                    val message = uri.getQueryParameter("scrollToKeywords")
                    if (!message.isNullOrEmpty()) {
                        mWebHolder.scrollToKeywords(message.split(","))
                    }
                }
            })
            .setInterceptUrlInterceptor { uri, reqHeaders, reqMethod ->
                val pageUri = Uri.parse(presenter.articleUrl)
                ReadingModeManager.getUrlRegexBeanForHost(pageUri.host)
                    ?: return@setInterceptUrlInterceptor null
                WebReadingModeInterceptor.intercept(
                    pageUri,
                    uri,
                    mWebHolder.userAgent,
                    reqHeaders,
                    reqMethod
                )?.let {
                    return@setInterceptUrlInterceptor it
                }
                WebResUrlInterceptor.intercept(
                    pageUri,
                    uri,
                    mWebHolder.userAgent,
                    reqHeaders,
                    reqMethod
                )?.let {
                    return@setInterceptUrlInterceptor it
                }
                return@setInterceptUrlInterceptor null
            }
            .setOnPageTitleCallback {
                presenter.updateReadRecordTitle(mWebHolder.url, it)
            }
            .setOnPageScrollEndListener {
                presenter.isReadLater { isReadLater ->
                    if (isReadLater) {
                        presenter.removeReadLater()
                    }
                }
            }
            .setOnPageScrollChangeListener {
                presenter.updateReadRecordPercent(mWebHolder.url, it)
            }
        binding.wc.setOnDoubleClickListener { _, _ ->
            changeRevealLayoutCenterXY(
                binding.activityArticleFloatBtn.rl.width * 0.5F,
                binding.activityArticleFloatBtn.rl.height * 0.5F
            )
            presenter.collect()
        }

        window.decorView.doOnLayout {
            showGuideDialogIfNeeded()
        }
    }

    private fun showArticleFooter() {
        RecommendManager.getInstance().getBean {
            it?.articleFooter?.let { articleFooter ->
                binding.llFooter.isVisible = true

                binding.tvFooterTitle.isVisible = !articleFooter.title.isNullOrBlank()
                binding.tvFooterTitle.text = articleFooter.title

                binding.ivFooterImage.isVisible = !articleFooter.url.isNullOrBlank()
                if (!articleFooter.url.isNullOrEmpty()) {
                    ImageLoader.image(binding.ivFooterImage, articleFooter.url)
                    binding.ivFooterImage.setOnClickListener {
                        Router.routeTo(articleFooter.route)
                    }
                }
            }
        }
    }

    private fun shareQrcode() {
        mWebHolder.getShareInfo { url, covers, title, desc ->
            ArticleShareDialog(this, covers, title, desc, url).show()
        }
    }

    override fun loadData() {
        lastUrlLoadTime = System.currentTimeMillis()
        mWebHolder.loadUrl(presenter.articleUrl)
        presenter.isReadLater { switchReadLaterIcon() }

        if (!CDKeyUtils.getInstance().isActive) {
            BannerAdProvider(this, this, GoogleAdUnitIds.ARTICLE_DETAILS_BOTTOM_BANNER_AD)
                .attachToContainer(binding.flAdContainer)
        }
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
        WebResUrlInterceptor.cancel()
        WebReadingModeInterceptor.cancel()
        floatIconsAnim?.cancel()
        floatIcons.forEach {
            it.tipAnim?.cancel()
        }
        mWebHolder.onDestroy(false)
        super.onDestroy()
    }

    private fun doFloatTipAnim(floatIconOnTouched: FloatIcon?) {
        floatIcons.forEach { floatIcon ->
            floatIcon.tipAnim?.cancel()
            if (floatIconOnTouched == floatIcon) {
                floatIcon.tipAnim = AnimatorSet().apply {
                    duration = 200L
                    interpolator = DecelerateInterpolator()
                    val fromX = if (floatIcon.tip.translationX == 0F) {
                        -floatIcon.icon.width.toFloat()
                    } else {
                        floatIcon.tip.translationX
                    }
                    val fromA = if (floatIcon.tip.alpha == 1F) {
                        0F
                    } else {
                        floatIcon.tip.alpha
                    }
                    playTogether(
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "translationX",
                            fromX, 0F
                        ),
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "alpha",
                            fromA, 1F
                        )
                    )
                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            floatIcon.tip.visible()
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }
                    })
                }
            } else {
                floatIcon.tipAnim = AnimatorSet().apply {
                    duration = 200L
                    interpolator = DecelerateInterpolator()
                    val fromX = floatIcon.tip.translationX
                    val fromA = floatIcon.tip.alpha
                    playTogether(
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "translationX",
                            fromX, -floatIcon.icon.width.toFloat()
                        ),
                        ObjectAnimator.ofFloat(
                            floatIcon.tip, "alpha",
                            fromA, 0F
                        )
                    )
                    addListener(object : Animator.AnimatorListener {
                        private var endByCancel = false

                        override fun onAnimationStart(animation: Animator) {
                            floatIcon.tip.visible()
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            if (endByCancel) return
                            if (floatIcon.tip.translationX != 0F) {
                                floatIcon.tip.invisible()
                            }
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            endByCancel = true
                        }
                    })
                }
            }
            floatIcon.tipAnim?.start()
        }
    }

    private fun toggleFloatIcons() {
        floatIconsVisible = !floatIconsVisible
        if (!floatIconsVisible) {
            doFloatTipAnim(null)
        }
        floatIconsAnim?.cancel()
        floatIconsAnim = AnimatorSet().apply {
            val anims = mutableListOf<Animator>()
            anims.add(
                ObjectAnimator.ofFloat(
                    binding.activityArticleFloatBtn.flBack,
                    "rotation",
                    binding.activityArticleFloatBtn.flBack.rotation,
                    if (floatIconsVisible) 360F else 0F
                ).apply {
                    duration = 300L
                    addUpdateListener {
                        if (it.animatedFraction > 0.5F) {
                            if (floatIconsVisible) binding.activityArticleFloatBtn.ivClose.visible()
                            else binding.activityArticleFloatBtn.ivClose.invisible()
                        }
                    }
                })
            floatIcons.filterIndexed { index, floatIconModel ->
                anims.add(AnimatorSet().apply {
                    duration = 300L
                    val isBegin = floatIconModel.container.translationY == 0F
                    playTogether(
                        ObjectAnimator.ofFloat(
                            floatIconModel.container, "translationY",
                            if (isBegin) 0F else floatIconModel.container.translationY,
                            if (floatIconsVisible) -floatIconModel.container.height.toFloat() * (index + 1) else 0F
                        ),
                        ObjectAnimator.ofFloat(
                            floatIconModel.shadow, "alpha",
                            if (isBegin) 0F else floatIconModel.shadow.alpha,
                            if (floatIconsVisible) 1F else 0F
                        )
                    )
                })
            }
            playTogether(anims)
            interpolator = DecelerateInterpolator()
        }
        floatIconsAnim?.apply {
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    floatIcons.forEach { it.container.visible() }
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    floatIcons.forEach {
                        if (it.container.translationY == 0F) it.container.invisible()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }
            })
        }?.start()
    }

    @SwipeBackDirection
    override fun swipeBackDirection(): Int {
        return SwipeBackDirection.RIGHT
    }

    override fun swipeBackForceEdge(): Boolean {
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        userTouched = true
        return super.dispatchTouchEvent(ev)
    }

    private fun changeRevealLayoutCenterXY(x: Float, y: Float) {
        binding.activityArticleFloatBtn.rl.setCenter(x, y)
        binding.activityArticleFloatBtn.cvCollect.setCenter(x, y)
    }

    private fun switchCollectView(anim: Boolean = true) {
        binding.activityArticleFloatBtn.rl.setChecked(presenter.collected, anim)
        binding.activityArticleFloatBtn.cvCollect.setChecked(presenter.collected, anim)
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

    override fun addReadLaterSuccess() {
        switchReadLaterIcon()
        ToastMaker.showShort(getString(R.string.added_to_my_bookmarks))
    }

    override fun addReadLaterFailed() {
        switchReadLaterIcon()
        ToastMaker.showShort(getString(R.string.failed_to_add_my_bookmarks))
    }

    override fun removeReadLaterSuccess() {
        switchReadLaterIcon()
        ToastMaker.showShort(getString(R.string.moved_out_of_my_bookmarks))
    }

    override fun removeReadLaterFailed() {
        switchReadLaterIcon()
        ToastMaker.showShort(getString(R.string.failed_to_move_out_of_my_bookmarks))
    }

    private fun switchReadLaterIcon() {
        if (presenter.readLater) {
            binding.activityArticleFloatBtn.aivReadLater.setImageResource(R.drawable.ic_read_later_added)
            binding.activityArticleFloatBtn.aivReadLater.setColorFilter(
                ResUtils.getThemeColor(
                    binding.activityArticleFloatBtn.aivReadLater,
                    R.attr.colorIconMain
                )
            )
        } else {
            binding.activityArticleFloatBtn.aivReadLater.setImageResource(R.drawable.ic_read_later)
            binding.activityArticleFloatBtn.aivReadLater.setColorFilter(
                ResUtils.getThemeColor(
                    binding.activityArticleFloatBtn.aivReadLater,
                    R.attr.colorIconSurface
                )
            )
        }
    }

    private fun showGuideDialogIfNeeded() {
        if (GuideSPUtils.getInstance().isArticleGuideShown) {
            return
        }
        window?.decorView?.post {
            showGuideBackBtnDialog {
                showGuideDoubleTapDialog {
                    showGuidePreviewImageDialog {
                        GuideSPUtils.getInstance().setArticleGuideShown()
                    }
                }
            }
        }
    }

    private fun showGuideBackBtnDialog(onDismiss: () -> Unit) {
        GuideLayer(this@ArticleActivity)
            .setBackgroundColorInt(
                ResUtils.getThemeColor(
                    binding.activityArticleFloatBtn.aivReadLater,
                    R.attr.colorDialogBg
                )
            )
            .addMapping(GuideLayer.Mapping().apply {
                setTargetView(binding.activityArticleFloatBtn.ivClose)
                cornerRadius = 9999F
                guideView = LayoutInflater.from(this@ArticleActivity)
                    .inflate(R.layout.dialog_guide_tip, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                            text =
                                getString(R.string.long_press_the_back_button_to_have_more_shortcut_menus)
                        }
                    }
                marginLeft = ResUtils.getDimens(R.dimen.margin_def).toInt()
                setHorizontalAlign(GuideLayer.Align.Horizontal.TO_RIGHT)
                setVerticalAlign(GuideLayer.Align.Vertical.CENTER)
            })
            .addMapping(GuideLayer.Mapping().apply {
                val cx = window?.decorView?.width ?: 0 / 2
                val cy = window?.decorView?.height ?: 0 / 2
                targetRect = Rect(cx, cy, cx, cy)
                guideView = LayoutInflater.from(this@ArticleActivity)
                    .inflate(R.layout.dialog_guide_btn, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                            text = getString(R.string.next)
                        }
                    }
                marginBottom = ResUtils.getDimens(R.dimen.margin_big).toInt()
                setHorizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                setVerticalAlign(GuideLayer.Align.Vertical.CENTER)
                addOnClickListener(Layer.OnClickListener { layer, _ ->
                    layer.dismiss()
                }, R.id.dialog_guide_tv_btn)
            })
            .addOnVisibleChangeListener(object : Layer.OnVisibleChangedListener {
                override fun onShow(layer: Layer) {
                }

                override fun onDismiss(layer: Layer) {
                    onDismiss.invoke()
                }
            })
            .show()
    }

    @SuppressLint("InflateParams")
    private fun showGuideDoubleTapDialog(onDismiss: () -> Unit) {
        GuideLayer(this@ArticleActivity)
            .setBackgroundColorInt(
                ResUtils.getThemeColor(
                    binding.activityArticleFloatBtn.aivReadLater,
                    R.attr.colorDialogBg
                )
            )
            .addMapping(GuideLayer.Mapping().apply {
                val cx = window?.decorView?.width ?: 0 / 2
                val cy = window?.decorView?.height ?: 0 / 2
                targetRect = Rect(cx, cy, cx, cy)
                guideView = LayoutInflater.from(this@ArticleActivity)
                    .inflate(R.layout.dialog_guide_tip, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                            text = getString(R.string.double_click_anywhere_to_quickly_bookmark)
                        }
                    }
                horizontalAlign = GuideLayer.Align.Horizontal.CENTER
                verticalAlign = GuideLayer.Align.Vertical.CENTER
            })
            .addMapping(GuideLayer.Mapping().apply {
                val cx = window?.decorView?.width ?: 0 / 2
                val cy = window?.decorView?.height ?: 0 / 2
                targetRect = Rect(cx, cy, cx, cy)
                guideView = LayoutInflater.from(this@ArticleActivity)
                    .inflate(R.layout.dialog_guide_btn, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                            text = getString(R.string.next)
                        }
                    }
                horizontalAlign = GuideLayer.Align.Horizontal.CENTER
                verticalAlign = GuideLayer.Align.Vertical.ALIGN_PARENT_BOTTOM
                marginBottom = ResUtils.getDimens(R.dimen.margin_big).toInt()
                addOnClickListener(Layer.OnClickListener { layer, _ ->
                    layer.dismiss()
                }, R.id.dialog_guide_tv_btn)
            })
            .addOnVisibleChangeListener(object : Layer.OnVisibleChangedListener {
                override fun onShow(layer: Layer) {
                }

                override fun onDismiss(layer: Layer) {
                    onDismiss.invoke()
                }
            })
            .show()
    }

    private fun showGuidePreviewImageDialog(onDismiss: () -> Unit) {
        GuideLayer(this@ArticleActivity)
            .setBackgroundColorInt(
                ResUtils.getThemeColor(
                    binding.activityArticleFloatBtn.aivReadLater,
                    R.attr.colorDialogBg
                )
            )
            .addMapping(GuideLayer.Mapping().apply {
                val cx = window?.decorView?.width ?: 0 / 2
                val cy = window?.decorView?.height ?: 0 / 2
                targetRect = Rect(cx, cy, cx, cy)
                guideView = LayoutInflater.from(this@ArticleActivity)
                    .inflate(R.layout.dialog_guide_tip, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                            text =
                                getString(R.string.long_press_the_web_image_to_preview_a_larger_image)
                        }
                    }
                horizontalAlign = GuideLayer.Align.Horizontal.CENTER
                verticalAlign = GuideLayer.Align.Vertical.CENTER
            })
            .addMapping(GuideLayer.Mapping().apply {
                val cx = window?.decorView?.width ?: 0 / 2
                val cy = window?.decorView?.height ?: 0 / 2
                targetRect = Rect(cx, cy, cx, cy)
                guideView = LayoutInflater.from(this@ArticleActivity)
                    .inflate(R.layout.dialog_guide_btn, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                            text = getString(R.string.i_know)
                        }
                    }
                horizontalAlign = GuideLayer.Align.Horizontal.CENTER
                verticalAlign = GuideLayer.Align.Vertical.ALIGN_PARENT_BOTTOM
                marginBottom = ResUtils.getDimens(R.dimen.margin_big).toInt()
                addOnClickListener(Layer.OnClickListener { layer, _ ->
                    layer.dismiss()
                }, R.id.dialog_guide_tv_btn)
            })
            .addOnVisibleChangeListener(object : Layer.OnVisibleChangedListener {
                override fun onShow(layer: Layer) {
                }

                override fun onDismiss(layer: Layer) {
                    onDismiss.invoke()
                }
            })
            .show()
    }
}