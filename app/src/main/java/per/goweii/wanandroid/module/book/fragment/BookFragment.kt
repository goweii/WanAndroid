package per.goweii.wanandroid.module.book.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.fragment_bookmark.*
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.event.CloseSecondFloorEvent
import per.goweii.wanandroid.module.book.activity.BookDetailsActivity
import per.goweii.wanandroid.module.book.adapter.BookAdapter
import per.goweii.wanandroid.module.book.model.BookBean
import per.goweii.wanandroid.module.book.contract.BookPresenter
import per.goweii.wanandroid.module.book.contract.BookView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.RvConfigUtils
import per.goweii.wanandroid.widget.refresh.SimpleOnMultiListener

class BookFragment : BaseFragment<BookPresenter>(), BookView {
    private lateinit var mAdapter: BookAdapter

    override fun getLayoutRes(): Int = R.layout.fragment_book

    override fun initPresenter(): BookPresenter = BookPresenter()

    override fun initView() {
        SmartRefreshUtils.with(srl).pureScrollMode()
        srl.setOnMultiListener(object : SimpleOnMultiListener() {
            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
                super.onFooterMoving(
                    footer,
                    isDragging,
                    percent,
                    offset,
                    footerHeight,
                    maxDragHeight
                )
                if (srl.state != RefreshState.PullUpCanceled && isDragging && percent > 1.2F) {
                    srl.closeHeaderOrFooter()
                    CloseSecondFloorEvent().post()
                }
            }
        })
        rv.layoutManager = GridLayoutManager(context, 3)
        mAdapter = BookAdapter()
        RvConfigUtils.init(mAdapter)
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.let { item ->
                BookDetailsActivity.start(requireContext(), item)
            }
        }
        rv.adapter = mAdapter
        MultiStateUtils.setEmptyAndErrorClick(msv) {
            MultiStateUtils.toLoading(msv)
            presenter.getList()
        }
    }

    override fun loadData() {
        MultiStateUtils.toLoading(msv)
    }

    override fun onVisible(isFirstVisible: Boolean) {
        super.onVisible(isFirstVisible)
        presenter.getList()
    }

    override fun onInvisible() {
        super.onInvisible()
    }

    override fun isRegisterEventBus(): Boolean = false

    override fun getBookListSuccess(list: List<BookBean>) {
        mAdapter.setNewData(list)
        if (list.isEmpty()) {
            MultiStateUtils.toEmpty(msv, true)
        } else {
            MultiStateUtils.toContent(msv)
        }
    }

    override fun getBookListFailed() {
        MultiStateUtils.toError(msv)
    }
}