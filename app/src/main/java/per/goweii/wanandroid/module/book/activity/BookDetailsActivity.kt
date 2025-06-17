package per.goweii.wanandroid.module.book.activity

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.utils.ResUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.ActivityBookDetailsBinding
import per.goweii.wanandroid.db.model.ReadRecordModel
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

class BookDetailsActivity : BaseActivity<BookDetailsPresenter, ActivityBookDetailsBinding>(), BookDetailsView {
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

    override fun initViewBinding(inflater: LayoutInflater) = ActivityBookDetailsBinding.inflate(inflater)

    override fun initPresenter(): BookDetailsPresenter = BookDetailsPresenter()

    override fun initView() {
        bookBean = intent.getParcelableExtra(PARAM_BOOK)!!

        ImageLoader.userIcon(binding.rivBookImg, bookBean.cover)
        binding.abc.titleTextView.text = bookBean.name
        binding.tvBookName.text = bookBean.name
        binding.tvBookAuthor.text = bookBean.author
        binding.tvBookDesc.text = bookBean.desc
        binding.tvBookCopyright.text = bookBean.lisense
        binding.tvBookCopyright.setOnClickListener {
            UrlOpenUtils.with(bookBean.lisenseLink).open(this)
        }

        binding.rv.layoutManager = LinearLayoutManager(context)
        adapter = BookChapterAdapter()
        adapter.setEnableLoadMore(false)
        adapter.setOnLoadMoreListener({
            presenter.getChapters(bookBean.id, currPage)
        }, binding.rv)
        adapter.setOnItemClickListener { _, _, position ->
            val item: BookChapterBean = adapter.getItem(position) ?: return@setOnItemClickListener
            UrlOpenUtils.with(item.articleBean).open(context)
        }
        binding.rv.adapter = adapter
        MultiStateUtils.setEmptyAndErrorClick(binding.msv) {
            MultiStateUtils.toLoading(binding.msv)
            presenter.getChapters(bookBean.id, currPage)
        }
        binding.abl.addOnOffsetChangedListener(OnOffsetChangedListener { abl, offset ->
            if (abs(offset) == abl.totalScrollRange) {
                binding.abc.titleTextView.alpha = 1f
                val color = ResUtils.getThemeColor(binding.abc, R.attr.colorMainOrSurface)
                binding.abc.setBackgroundColor(color)
                binding.llTop.alpha = 1F
            } else {
                binding.abc.titleTextView.alpha = 0f
                val color = ResUtils.getThemeColor(binding.abc, R.attr.colorTransparent)
                binding.abc.setBackgroundColor(color)
                binding.llTop.alpha = 1f - (abs(offset).toFloat() / abl.totalScrollRange.toFloat())
            }
        })
        binding.ctbl.post {
            binding.ctbl.minimumHeight = binding.abc.actionBar.height
            binding.ctbl.scrimVisibleHeightTrigger = binding.abc.actionBar.height
        }
    }

    override fun loadData() {
        MultiStateUtils.toLoading(binding.msv)
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
                    if (event.time != null) {
                        bookChapterBean.time = event.time
                    }
                    if (event.percent != null) {
                        bookChapterBean.percent =
                            (event.percent.toFloat() / ReadRecordModel.MAX_PERCENT)
                                .coerceIn(0f, 1f)
                    }
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
                toEmpty(binding.msv)
            } else {
                toContent(binding.msv)
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
            toError(binding.msv)
        }
    }
}