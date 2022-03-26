package per.goweii.wanandroid.module.book.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.activity_book_intro.*
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.utils.ResUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.book.adapter.BookChapterAdapter
import per.goweii.wanandroid.module.book.bean.BookIntroBean
import per.goweii.wanandroid.module.book.contract.BookIntroPresenter
import per.goweii.wanandroid.module.book.contract.BookIntroView
import per.goweii.wanandroid.utils.ImageLoader
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.UrlOpenUtils
import kotlin.math.abs

class BookIntroActivity : BaseActivity<BookIntroPresenter>(), BookIntroView {
    companion object {
        private const val PARAM_LINK = "link"

        fun start(context: Context, link: String) {
            context.startActivity(Intent(context, BookIntroActivity::class.java).apply {
                putExtra(PARAM_LINK, link)
            })
        }
    }

    private lateinit var mLink: String
    private lateinit var mAdapter: BookChapterAdapter

    override fun getLayoutId(): Int = R.layout.activity_book_intro

    override fun initPresenter(): BookIntroPresenter = BookIntroPresenter()

    override fun initView() {
        mLink = intent.getStringExtra(PARAM_LINK) ?: ""
        rv.layoutManager = LinearLayoutManager(context)
        mAdapter = BookChapterAdapter()
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnItemClickListener { _, _, position ->
            val item: BookIntroBean.BookChapterBean =
                mAdapter.getItem(position) ?: return@setOnItemClickListener
            UrlOpenUtils.with(item.link).open(context)
        }
        rv.adapter = mAdapter
        MultiStateUtils.setEmptyAndErrorClick(msv) {
            MultiStateUtils.toLoading(msv)
            presenter.getIntro(mLink)
        }
        abl.addOnOffsetChangedListener(OnOffsetChangedListener { abl, offset ->
            if (abs(offset) == abl.totalScrollRange) {
                abc.titleTextView.alpha = 1f
                val color = ResUtils.getThemeColor(abc, R.attr.colorMainOrSurface)
                abc.setBackgroundColor(color)
                ll_top.alpha = 1F
            } else {
                abc.titleTextView.alpha = 0f
                val color = ResUtils.getThemeColor(abc, R.attr.colorTransparent)
                abc.setBackgroundColor(color)
                ll_top.alpha = 1f - (abs(offset).toFloat() / abl.totalScrollRange.toFloat())
            }
        })
        ctbl.post {
            ctbl.minimumHeight = abc.actionBar.height
            ctbl.scrimVisibleHeightTrigger = abc.actionBar.height
        }
    }

    override fun loadData() {
        MultiStateUtils.toLoading(msv)
        msv_list.visibility = View.GONE
        presenter.getIntro(mLink)
    }

    override fun getBookIntroSuccess(bean: BookIntroBean) {
        MultiStateUtils.toContent(msv)
        ImageLoader.userIcon(riv_book_img, bean.img)
        abc.titleTextView.text = bean.name
        tv_book_name.text = bean.name
        tv_book_author.text = bean.author
        tv_book_desc.text = bean.desc
        tv_book_copyright.text = bean.copyright.name
        tv_book_copyright.setOnClickListener {
            UrlOpenUtils.with(bean.copyright.url).open(this)
        }
        mAdapter.setNewData(bean.chapters)
        msv_list.visibility = View.VISIBLE
        if (bean.chapters.isEmpty()) {
            MultiStateUtils.toEmpty(msv_list)
        } else {
            MultiStateUtils.toContent(msv_list)
        }
    }

    override fun getBookIntroListFailed() {
        MultiStateUtils.toError(msv)
    }
}