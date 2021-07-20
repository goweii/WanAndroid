package per.goweii.wanandroid.module.main.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.activity_article_comment.*
import kotlinx.android.synthetic.main.activity_article_content.*
import kotlinx.android.synthetic.main.activity_article_float_btn.*
import per.goweii.anylayer.Layer
import per.goweii.anylayer.guide.GuideLayer
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.ResUtils
import per.goweii.basic.utils.ShareUtils
import per.goweii.basic.utils.ext.invisible
import per.goweii.basic.utils.ext.visible
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.wanandroid.R
import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.module.home.activity.UserPageActivity
import per.goweii.wanandroid.module.main.adapter.ArticleCommentAdapter
import per.goweii.wanandroid.module.main.dialog.CommentInputDialog
import per.goweii.wanandroid.module.main.dialog.QrcodeShareDialog
import per.goweii.wanandroid.module.main.model.CmsCommentResp
import per.goweii.wanandroid.module.main.model.CommentItemEntity
import per.goweii.wanandroid.module.main.presenter.ArticlePresenter
import per.goweii.wanandroid.module.main.utils.BottomDrawerViewOutlineProvider
import per.goweii.wanandroid.module.main.utils.FloatIconTouchListener
import per.goweii.wanandroid.module.main.view.ArticleView
import per.goweii.wanandroid.utils.GuideSPUtils
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.NightModeUtils
import per.goweii.wanandroid.utils.UrlOpenUtils
import per.goweii.wanandroid.utils.web.WebHolder
import per.goweii.wanandroid.utils.web.WebHolder.with
import per.goweii.wanandroid.utils.web.interceptor.WebReadingModeInterceptor
import per.goweii.wanandroid.utils.web.interceptor.WebResUrlInterceptor

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticleActivity : BaseActivity<ArticlePresenter>(), ArticleView, SwipeBackAbility.OnlyEdge {

    private data class FloatIcon(
            val container: View,
            val shadow: View,
            val icon: View,
            val tip: View,
            var tipAnim: Animator? = null
    )

    companion object {
        const val LIMIT = 20

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

    private var floatIconsVisible = false
    private var floatIconsAnim: AnimatorSet? = null
    private val floatIcons: List<FloatIcon> by lazy {
        mutableListOf<FloatIcon>().apply {
            add(FloatIcon(rl_icon_collect, sl_collect, cv_collect, tv_collect_tip))
            add(FloatIcon(rl_icon_read_later, sl_read_later, aiv_read_later, tv_read_later_tip))
            add(FloatIcon(rl_icon_open, sl_open, aiv_open, tv_open_tip))
            add(FloatIcon(rl_icon_share, sl_share, aiv_share, tv_share_tip))
        }
    }

    private var dlOutlineProvider: BottomDrawerViewOutlineProvider? = null

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
        val icons = mutableListOf<FloatIconTouchListener.Icon>()
        floatIcons.forEach {
            icons.add(FloatIconTouchListener.Icon(it.icon))
        }
        v_back.setOnTouchListener(FloatIconTouchListener(icons, object : FloatIconTouchListener.OnFloatTouchedListener {
            override fun onTouched(v: View?) {
                var floatIcon: FloatIcon? = null
                floatIcons.forEach {
                    if (it.icon == v) {
                        floatIcon = it
                    }
                }
                doFloatTipAnim(floatIcon)
            }
        }))
        v_back.setOnClickListener {
            if (floatIconsVisible) toggleFloatIcons()
            else finish()
        }
        v_back.setOnLongClickListener {
            toggleFloatIcons()
            return@setOnLongClickListener true
        }
        aiv_share.setOnClickListener {
            QrcodeShareDialog(this, presenter.articleUrl, presenter.articleTitle)
                    .setOnShareClickListener {
                        ShareUtils.shareBitmap(this, it)
                    }
                    .show()
            if (floatIconsVisible) toggleFloatIcons()
        }
        aiv_read_later.setOnClickListener {
            presenter.isReadLater { isReadLater ->
                if (isReadLater) {
                    presenter.removeReadLater()
                } else {
                    presenter.addReadLater()
                }
            }
            if (floatIconsVisible) toggleFloatIcons()
        }
        aiv_open.setOnClickListener {
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
        cv_collect.setOnClickListener {
            if (cv_collect.isChecked) {
                presenter.collect()
            } else {
                presenter.uncollect()
            }
            if (floatIconsVisible) toggleFloatIcons()
        }
        wc.setOnTouchDownListener {
            if (floatIconsVisible) toggleFloatIcons()
        }
        mWebHolder = with(this, wc, pb)
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
                    }

                    override fun onPageFinished() {
                        isPageLoadFinished = true
                    }
                })
                .setInterceptUrlInterceptor { uri, reqHeaders, reqMethod ->
                    WebReadingModeInterceptor.intercept(uri, mWebHolder.userAgent, reqHeaders, reqMethod)?.let {
                        return@setInterceptUrlInterceptor it
                    }
                    WebResUrlInterceptor.intercept(uri, mWebHolder.userAgent, reqHeaders, reqMethod)?.let {
                        return@setInterceptUrlInterceptor it
                    }
                    return@setInterceptUrlInterceptor null
                }
                .setOnPageTitleCallback {
                    presenter.readRecord(mWebHolder.url, mWebHolder.title)
                }
                .setOnPageScrollEndListener {
                    presenter.isReadLater { isReadLater ->
                        if (isReadLater) {
                            presenter.removeReadLater()
                        }
                    }
                }
        wc.setOnDoubleClickListener { _, _ ->
            if (rl != null) {
                changeRevealLayoutCenterXY(rl.width * 0.5F, rl.height * 0.5F)
            }
            presenter.collect()
        }

        rv.layoutManager = LinearLayoutManager(context)
        adapter = ArticleCommentAdapter().apply {
            setOnItemClickListener { _, _, position ->
                adapter.getItem(position)?.let { item ->
                    when (item.itemType) {
                        CommentItemEntity.TYPE_COMMENT_ROOT -> {
                            presenter.commentReply = item
                            showCommentInputDialog()
                        }
                        CommentItemEntity.TYPE_COMMENT_CHILD -> {
                            presenter.commentReply = item
                            showCommentInputDialog()
                        }
                        CommentItemEntity.TYPE_COMMENT_LOAD -> {
                            if (item.loading) {
                                // do nothing
                            } else {
                                if (item.hasMore) {
                                    item.loading = true
                                    adapter.notifyItemChanged(position)
                                    presenter.commentReplys(item, item.commentRoot!!, item.offset, item.limit)
                                } else {
                                    // do nothing
                                }
                            }
                        }
                    }
                }
            }
        }
        rv.adapter = adapter
        adapter.setEnableLoadMore(true)
        adapter.setOnLoadMoreListener({
            presenter.comments(adapter.data.size, LIMIT)
        }, rv)
        MultiStateUtils.setEmptyAndErrorClick(msv) {
            MultiStateUtils.toLoading(msv)
            presenter.comments(adapter.data.size, LIMIT)
        }
        fl_top_bar_handle.setOnClickListener {
            dl.toggle()
        }
        tv_user_name.setOnClickListener {
            UserPageActivity.start(context, presenter.userId)
        }
        dlOutlineProvider = BottomDrawerViewOutlineProvider(ResUtils.getDimens(R.dimen.round_radius)).apply {
            setToView(dl.dragView)
        }
        dl.onOpened { }
        dl.onClosed { }
        dl.onDragging {
            if (floatIconsVisible) toggleFloatIcons()
            v_mask.alpha = 1F - it
            dlOutlineProvider?.updateFaction(1F - it)
        }
        rl_comment.setOnClickListener {
            presenter.commentReply = null
            showCommentInputDialog()
        }
        window.decorView.doOnLayout {
            showGuideDialogIfNeeded()
        }
        if (!CmsApi.isEnabled) {
            dl.setCloseHeight(0)
            wc.layoutParams.let {
                it as RelativeLayout.LayoutParams
                it.removeRule(RelativeLayout.ABOVE)
                it.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            wc.requestLayout()
        }
    }

    override fun loadData() {
        lastUrlLoadTime = System.currentTimeMillis()
        mWebHolder.loadUrl(presenter.articleUrl)
        tv_user_name.text = if (presenter.userName.isNotEmpty()) presenter.userName else "匿名"
        MultiStateUtils.toLoading(msv)
        presenter.isReadLater { switchReadLaterIcon() }
        if (CmsApi.isEnabled) {
            presenter.commentCount()
            presenter.comments(adapter.data.size, LIMIT)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (dl.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
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
                                    fromX, 0F),
                            ObjectAnimator.ofFloat(
                                    floatIcon.tip, "alpha",
                                    fromA, 1F)
                    )
                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                            floatIcon.tip.visible()
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                        }

                        override fun onAnimationCancel(animation: Animator?) {
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
                                    fromX, -floatIcon.icon.width.toFloat()),
                            ObjectAnimator.ofFloat(
                                    floatIcon.tip, "alpha",
                                    fromA, 0F)
                    )
                    addListener(object : Animator.AnimatorListener {
                        private var endByCancel = false

                        override fun onAnimationStart(animation: Animator?) {
                            floatIcon.tip.visible()
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            if (endByCancel) return
                            if (floatIcon.tip.translationX != 0F) {
                                floatIcon.tip.invisible()
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
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
            anims.add(ObjectAnimator.ofFloat(
                    fl_back, "rotation",
                    fl_back.rotation, if (floatIconsVisible) 360F else 0F).apply {
                duration = 300L
                addUpdateListener {
                    if (it.animatedFraction > 0.5F) {
                        if (floatIconsVisible) iv_close?.visible()
                        else iv_close?.invisible()
                    }
                }
            })
            floatIcons.filterIndexed { index, floatIconModel ->
                anims.add(AnimatorSet().apply {
                    duration = 300L
                    playTogether(
                            ObjectAnimator.ofFloat(
                                    floatIconModel.container, "translationY",
                                    floatIconModel.container.translationY,
                                    if (floatIconsVisible) -floatIconModel.container.height.toFloat() * (index + 1) else 0F),
                            ObjectAnimator.ofFloat(
                                    floatIconModel.shadow, "alpha",
                                    floatIconModel.shadow.alpha, if (floatIconsVisible) 1F else 0F)
                    )
                })
            }
            playTogether(anims)
            interpolator = DecelerateInterpolator()
        }
        floatIconsAnim?.apply {
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    floatIcons.forEach { it.container.visible() }
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    floatIcons.forEach {
                        if (it.container.translationY == 0F) it.container.invisible()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }
            })
        }?.start()
    }

    private fun setCommentCount(count: Int) {
        val countStr = if (count < 100) "$count" else "99+"
        tv_comment_count.text = "评论($countStr)"
    }

    override fun swipeBackOnlyEdge(): Boolean {
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        userTouched = true
        return super.dispatchTouchEvent(ev)
    }

    private fun changeRevealLayoutCenterXY(x: Float, y: Float) {
        rl.setCenter(x, y)
        cv_collect.setCenter(x, y)
    }

    private fun switchCollectView(anim: Boolean = true) {
        rl.setChecked(presenter.collected, anim)
        cv_collect.setChecked(presenter.collected, anim)
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
        ToastMaker.showShort("已加入我的书签")
    }

    override fun addReadLaterFailed() {
        switchReadLaterIcon()
        ToastMaker.showShort("加入我的书签失败")
    }

    override fun removeReadLaterSuccess() {
        switchReadLaterIcon()
        ToastMaker.showShort("已移出我的书签")
    }

    override fun removeReadLaterFailed() {
        switchReadLaterIcon()
        ToastMaker.showShort("移出我的书签失败")
    }

    private fun switchReadLaterIcon() {
        if (presenter.readLater) {
            aiv_read_later.setImageResource(R.drawable.ic_read_later_added)
            aiv_read_later.setColorFilter(ResUtils.getThemeColor(aiv_read_later, R.attr.colorIconMain))
        } else {
            aiv_read_later.setImageResource(R.drawable.ic_read_later)
            aiv_read_later.setColorFilter(ResUtils.getThemeColor(aiv_read_later, R.attr.colorIconSurface))
        }
    }

    override fun commentCountSuccess(resp: Int) {
        setCommentCount(resp)
    }

    override fun commentCountFailed(msg: String) {
        setCommentCount(0)
    }

    override fun commentSuccess(resp: CmsCommentResp) {
        MultiStateUtils.toContent(msv)
        presenter.commentReply?.let { reply ->
            val rootIndex = adapter.data.indexOf(reply.rootItem)
            val entity = CommentItemEntity.createTypeComment(resp)
            entity.rootItem = reply.rootItem
            entity.loadItem = reply.loadItem
            entity.loadItem?.let { loadItem ->
                loadItem.offset += 1
            }
            adapter.addData(rootIndex + 1, entity)
        } ?: run {
            val list = mutableListOf<CommentItemEntity>()
            val entity = CommentItemEntity.createTypeComment(resp)
            val load = CommentItemEntity.createTypeLoad(resp.id)
            entity.rootItem = entity
            entity.loadItem = load
            load.rootItem = entity
            load.loadItem = load
            list.add(entity)
            list.add(load)
            adapter.addData(0, list)
        }
        presenter.commentContent = null
        presenter.commentReply = null
    }

    override fun commentFailed(msg: String) {
        ToastMaker.showShort(msg)
    }

    override fun commentsSuccess(resp: List<CmsCommentResp>) {
        val list = mutableListOf<CommentItemEntity>()
        resp.forEach {
            val entity = CommentItemEntity.createTypeComment(it)
            val load = CommentItemEntity.createTypeLoad(it.id)
            entity.rootItem = entity
            entity.loadItem = load
            load.rootItem = entity
            load.loadItem = load
            list.add(entity)
            list.add(load)
        }
        adapter.addData(list)
        if (adapter.data.size == 0) {
            MultiStateUtils.toEmpty(msv)
        } else {
            MultiStateUtils.toContent(msv)
        }
        adapter.loadMoreComplete()
        if (resp.size < LIMIT) {
            adapter.loadMoreEnd()
        }
    }

    override fun commentsFailed(msg: String) {
        if (adapter.data.size == 0) {
            MultiStateUtils.toEmpty(msv)
        }
    }

    override fun commentsReplysSuccess(item: CommentItemEntity, resp: List<CmsCommentResp>) {
        val list = mutableListOf<CommentItemEntity>()
        resp.forEach {
            val entity = CommentItemEntity.createTypeComment(it)
            entity.rootItem = item.rootItem
            entity.loadItem = item.loadItem
            list.add(entity)
        }
        item.hasMore = resp.size >= 3
        item.loading = false
        item.offset += resp.size
        val index = adapter.data.indexOf(item)
        adapter.notifyItemChanged(index)
        adapter.addData(index, list)
    }

    override fun commentsReplysFailed(item: CommentItemEntity) {
        item.loading = false
        val index = adapter.data.indexOf(item)
        adapter.notifyItemChanged(index)
    }

    private fun showCommentInputDialog() {
        CommentInputDialog(context,
                presenter.commentContent,
                presenter.commentReply,
                { presenter.commentContent = it },
                { presenter.comment() })
                .show()
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
                .backgroundColorInt(ResUtils.getThemeColor(aiv_read_later, R.attr.colorDialogBg))
                .mapping(GuideLayer.Mapping().apply {
                    targetView(iv_close)
                    cornerRadius(9999F)
                    guideView(LayoutInflater.from(this@ArticleActivity)
                            .inflate(R.layout.dialog_guide_tip, null, false).apply {
                                findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                                    text = "长按返回按钮有更多快捷菜单~"
                                }
                            })
                    marginLeft(ResUtils.getDimens(R.dimen.margin_def).toInt())
                    horizontalAlign(GuideLayer.Align.Horizontal.TO_RIGHT)
                    verticalAlign(GuideLayer.Align.Vertical.CENTER)
                })
                .mapping(GuideLayer.Mapping().apply {
                    val cx = window?.decorView?.width ?: 0 / 2
                    val cy = window?.decorView?.height ?: 0 / 2
                    targetRect(Rect(cx, cy, cx, cy))
                    guideView(LayoutInflater.from(this@ArticleActivity)
                            .inflate(R.layout.dialog_guide_btn, null, false).apply {
                                findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                                    text = "下一个"
                                }
                            })
                    horizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                    verticalAlign(GuideLayer.Align.Vertical.CENTER)
                    marginBottom(ResUtils.getDimens(R.dimen.margin_big).toInt())
                    onClick(Layer.OnClickListener { layer, _ ->
                        layer.dismiss()
                    }, R.id.dialog_guide_tv_btn)
                })
                .onVisibleChangeListener(object : Layer.OnVisibleChangeListener {
                    override fun onShow(layer: Layer) {
                    }

                    override fun onDismiss(layer: Layer) {
                        onDismiss.invoke()
                    }
                })
                .show()
    }

    private fun showGuideDoubleTapDialog(onDismiss: () -> Unit) {
        GuideLayer(this@ArticleActivity)
                .backgroundColorInt(ResUtils.getThemeColor(aiv_read_later, R.attr.colorDialogBg))
                .mapping(GuideLayer.Mapping().apply {
                    val cx = window?.decorView?.width ?: 0 / 2
                    val cy = window?.decorView?.height ?: 0 / 2
                    targetRect(Rect(cx, cy, cx, cy))
                    guideView(LayoutInflater.from(this@ArticleActivity)
                            .inflate(R.layout.dialog_guide_tip, null, false).apply {
                                findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                                    text = "双击任意位置可快速收藏~"
                                }
                            })
                    horizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                    verticalAlign(GuideLayer.Align.Vertical.CENTER)
                })
                .mapping(GuideLayer.Mapping().apply {
                    val cx = window?.decorView?.width ?: 0 / 2
                    val cy = window?.decorView?.height ?: 0 / 2
                    targetRect(Rect(cx, cy, cx, cy))
                    guideView(LayoutInflater.from(this@ArticleActivity)
                            .inflate(R.layout.dialog_guide_btn, null, false).apply {
                                findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                                    text = "下一个"
                                }
                            })
                    horizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                    verticalAlign(GuideLayer.Align.Vertical.ALIGN_PARENT_BOTTOM)
                    marginBottom(ResUtils.getDimens(R.dimen.margin_big).toInt())
                    onClick(Layer.OnClickListener { layer, _ ->
                        layer.dismiss()
                    }, R.id.dialog_guide_tv_btn)
                })
                .onVisibleChangeListener(object : Layer.OnVisibleChangeListener {
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
                .backgroundColorInt(ResUtils.getThemeColor(aiv_read_later, R.attr.colorDialogBg))
                .mapping(GuideLayer.Mapping().apply {
                    val cx = window?.decorView?.width ?: 0 / 2
                    val cy = window?.decorView?.height ?: 0 / 2
                    targetRect(Rect(cx, cy, cx, cy))
                    guideView(LayoutInflater.from(this@ArticleActivity)
                            .inflate(R.layout.dialog_guide_tip, null, false).apply {
                                findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                                    text = "长按网页图片可预览大图~"
                                }
                            })
                    horizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                    verticalAlign(GuideLayer.Align.Vertical.CENTER)
                })
                .mapping(GuideLayer.Mapping().apply {
                    val cx = window?.decorView?.width ?: 0 / 2
                    val cy = window?.decorView?.height ?: 0 / 2
                    targetRect(Rect(cx, cy, cx, cy))
                    guideView(LayoutInflater.from(this@ArticleActivity)
                            .inflate(R.layout.dialog_guide_btn, null, false).apply {
                                findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                                    text = "我知道了"
                                }
                            })
                    horizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                    verticalAlign(GuideLayer.Align.Vertical.ALIGN_PARENT_BOTTOM)
                    marginBottom(ResUtils.getDimens(R.dimen.margin_big).toInt())
                    onClick(Layer.OnClickListener { layer, _ ->
                        layer.dismiss()
                    }, R.id.dialog_guide_tv_btn)
                })
                .onVisibleChangeListener(object : Layer.OnVisibleChangeListener {
                    override fun onShow(layer: Layer) {
                    }

                    override fun onDismiss(layer: Layer) {
                        onDismiss.invoke()
                    }
                })
                .show()
    }
}