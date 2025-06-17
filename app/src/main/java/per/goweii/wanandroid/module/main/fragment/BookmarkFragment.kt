package per.goweii.wanandroid.module.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.constant.RefreshState
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.FragmentBookmarkBinding
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

class BookmarkFragment : BaseFragment<BookmarkPresenter, FragmentBookmarkBinding>(), BookmarkView {

    private lateinit var mAdapter: BookmarkAdapter

    private var offset = 0
    private val perPageCount = 20

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookmarkBinding {
        return FragmentBookmarkBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): BookmarkPresenter = BookmarkPresenter()

    override fun initView() {
        SmartRefreshUtils.with(binding.srl).pureScrollMode()
        binding.srl.setOnMultiListener(object : SimpleOnMultiListener() {
            override fun onFooterMoving(footer: RefreshFooter?, isDragging: Boolean, percent: Float, offset: Int, footerHeight: Int, maxDragHeight: Int) {
                super.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight)
                if (binding.srl.state != RefreshState.PullUpCanceled && isDragging && percent > 1.2F) {
                    binding.srl.closeHeaderOrFooter()
                    CloseSecondFloorEvent().post()
                }
            }
        })
        binding.rv.layoutManager = LinearLayoutManager(context)
        mAdapter = BookmarkAdapter()
        RvConfigUtils.init(mAdapter)
        mAdapter.setEnableLoadMore(true)
        mAdapter.setOnLoadMoreListener({ getPageList() }, binding.rv)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.let { item ->
                UrlOpenUtils.with(item.link).open(context)
            }
        }
        binding.rv.adapter = mAdapter
        setEmptyAndErrorClick(binding.msv) {
            toLoading(binding.msv)
            offset = 0
            getPageList()
        }
    }

    override fun loadData() {
        toLoading(binding.msv)
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
                toEmpty(binding.msv, true)
            } else {
                toContent(binding.msv)
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
            toError(binding.msv)
        } else {
            mAdapter.loadMoreFail()
        }
    }
}