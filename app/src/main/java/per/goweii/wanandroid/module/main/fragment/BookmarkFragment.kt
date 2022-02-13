package per.goweii.wanandroid.module.main.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.fragment_bookmark.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.db.model.ReadLaterModel
import per.goweii.wanandroid.event.CloseSecondFloorEvent
import per.goweii.wanandroid.event.ReadLaterEvent
import per.goweii.wanandroid.module.main.adapter.BookmarkAdapter
import per.goweii.wanandroid.module.main.contract.BookmarkPresenter
import per.goweii.wanandroid.module.main.contract.BookmarkView
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.setEmptyAndErrorClick
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toContent
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toEmpty
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toError
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.toLoading
import per.goweii.wanandroid.utils.RvConfigUtils
import per.goweii.wanandroid.utils.UrlOpenUtils
import per.goweii.wanandroid.widget.refresh.SimpleOnMultiListener

class BookmarkFragment : BaseFragment<BookmarkPresenter>(), BookmarkView {

    private lateinit var mAdapter: BookmarkAdapter

    private var offset = 0
    private val perPageCount = 20

    override fun getLayoutRes(): Int = R.layout.fragment_bookmark

    override fun initPresenter(): BookmarkPresenter = BookmarkPresenter()

    override fun initView() {
        SmartRefreshUtils.with(srl).pureScrollMode()
        srl.setOnMultiListener(object : SimpleOnMultiListener() {
            override fun onFooterMoving(footer: RefreshFooter?, isDragging: Boolean, percent: Float, offset: Int, footerHeight: Int, maxDragHeight: Int) {
                super.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight)
                if (srl.state != RefreshState.PullUpCanceled && isDragging && percent > 1.2F) {
                    srl.closeHeaderOrFooter()
                    CloseSecondFloorEvent().post()
                }
            }
        })
        rv.layoutManager = LinearLayoutManager(context)
        mAdapter = BookmarkAdapter()
        RvConfigUtils.init(mAdapter)
        mAdapter.setEnableLoadMore(true)
        mAdapter.setOnLoadMoreListener({ getPageList() }, rv)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.let { item ->
                UrlOpenUtils.with(item.link).open(context)
            }
        }
        rv.adapter = mAdapter
        setEmptyAndErrorClick(msv) {
            toLoading(msv)
            offset = 0
            getPageList()
        }
    }

    override fun loadData() {
        toLoading(msv)
    }

    override fun onVisible(isFirstVisible: Boolean) {
        super.onVisible(isFirstVisible)
        offset = 0
        getPageList()
    }

    override fun onInvisible() {
        super.onInvisible()
    }

    override fun isRegisterEventBus(): Boolean = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadLaterEvent(event: ReadLaterEvent) {
        if (!isAdded) return
        offset = 0
        presenter.getList(0, perPageCount)
    }

    private fun getPageList() {
        presenter.getList(offset, perPageCount)
    }

    override fun getBookmarkListSuccess(list: List<ReadLaterModel>) {
        if (offset == 0) {
            mAdapter.setNewData(list)
            if (list.isEmpty()) {
                toEmpty(msv, true)
            } else {
                toContent(msv)
            }
        } else {
            mAdapter.addData(list)
            mAdapter.loadMoreComplete()
        }
        offset = mAdapter.data.size
        if (list.size < perPageCount) {
            mAdapter.loadMoreEnd()
        }
    }

    override fun getBookmarkListFailed() {
        if (offset == 0) {
            toError(msv)
        } else {
            mAdapter.loadMoreFail()
        }
    }
}