package per.goweii.wanandroid.module.book.activity

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.activity_book_details.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.utils.ResUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.event.ReadRecordAddedEvent
import per.goweii.wanandroid.event.ReadRecordUpdateEvent
import per.goweii.wanandroid.module.book.adapter.BookChapterAdapter
import per.goweii.wanandroid.module.book.contract.BookDetailsPresenter
import per.goweii.wanandroid.module.book.contract.BookDetailsView
import per.goweii.wanandroid.module.book.model.BookBean
import per.goweii.wanandroid.module.book.model.BookChapterBean
import per.goweii.wanandroid.utils.ImageLoader
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toContent
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toEmpty
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toError
import per.goweii.wanandroid.utils.UrlOpenUtils
import kotlin.math.abs

class BookDetailsActivity : BaseActivity<BookDetailsPresenter>(), BookDetailsView {
    companion object {
        private const val PAGE_START = 0
        private const val PARAM_BOOK = "book"

        fun start(context: Context, bookBean: BookBean) {
            context.startActivity(Intent(context, BookDetailsActivity::class.java).apply {
                putExtra(PARAM_BOOK, bookBean as Parcelable)
            })
        }
    }

    private lateinit var bookBean: BookBean
    private lateinit var adapter: BookChapterAdapter

    private var currPage = PAGE_START

    override fun getLayoutId(): Int = R.layout.activity_book_details

    override fun initPresenter(): BookDetailsPresenter = BookDetailsPresenter()

    override fun initView() {
        bookBean = intent.getParcelableExtra(PARAM_BOOK)!!

        ImageLoader.userIcon(riv_book_img, bookBean.cover)
        abc.titleTextView.text = bookBean.name
        tv_book_name.text = bookBean.name
        tv_book_author.text = bookBean.author
        tv_book_desc.text = bookBean.desc
        tv_book_copyright.text = bookBean.lisense
        tv_book_copyright.setOnClickListener {
            UrlOpenUtils.with(bookBean.lisenseLink).open(this)
        }

        rv.layoutManager = LinearLayoutManager(context)
        adapter = BookChapterAdapter()
        adapter.setEnableLoadMore(false)
        adapter.setOnLoadMoreListener({
            presenter.getChapters(bookBean.id, currPage)
        }, rv)
        adapter.setOnItemClickListener { _, _, position ->
            val item: BookChapterBean = adapter.getItem(position) ?: return@setOnItemClickListener
            UrlOpenUtils.with(item.articleBean).open(context)
        }
        rv.adapter = adapter
        MultiStateUtils.setEmptyAndErrorClick(msv) {
            MultiStateUtils.toLoading(msv)
            presenter.getChapters(bookBean.id, currPage)
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
        presenter.getChapters(bookBean.id, currPage)
    }

    override fun isRegisterEventBus(): Boolean = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadRecordAddedEvent(event: ReadRecordAddedEvent) {
        kotlin.run {
            adapter.data.forEachIndexed { index, bookChapterBean ->
                if (bookChapterBean.articleBean.link == event.readRecordModel.link) {
                    bookChapterBean.time = event.readRecordModel.time
                    bookChapterBean.percent = event.readRecordModel.percentFloat
                    adapter.notifyItemChanged(index)
                    return@run
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadRecordUpdateEvent(event: ReadRecordUpdateEvent) {
        kotlin.run {
            adapter.data.forEachIndexed { index, bookChapterBean ->
                if (bookChapterBean.articleBean.link == event.link) {
                    bookChapterBean.time = event.time
                    bookChapterBean.percent = event.percent
                    adapter.notifyItemChanged(index)
                    return@run
                }
            }
        }
    }

    override fun getBookChaptersSuccess(
        list: List<BookChapterBean>,
        curPage: Int,
        isOver: Boolean
    ) {
        currPage = curPage + PAGE_START
        if (curPage == 1) {
            adapter.setNewData(list)
            adapter.setEnableLoadMore(true)
            if (list.isEmpty()) {
                toEmpty(msv)
            } else {
                toContent(msv)
            }
        } else {
            adapter.addData(list)
            adapter.loadMoreComplete()
        }
        if (isOver) {
            adapter.loadMoreEnd()
        }
    }

    override fun getBookChaptersFailed() {
        adapter.loadMoreFail()
        if (currPage == PAGE_START) {
            toError(msv)
        }
    }
}