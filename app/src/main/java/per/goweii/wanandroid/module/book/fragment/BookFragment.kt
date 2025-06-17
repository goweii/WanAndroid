package per.goweii.wanandroid.module.book.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.constant.RefreshState
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.wanandroid.databinding.FragmentBookBinding
import per.goweii.wanandroid.event.CloseSecondFloorEvent
import per.goweii.wanandroid.module.book.activity.BookDetailsActivity
import per.goweii.wanandroid.module.book.adapter.BookAdapter
import per.goweii.wanandroid.module.book.model.BookBean
import per.goweii.wanandroid.module.book.contract.BookPresenter
import per.goweii.wanandroid.module.book.contract.BookView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.RvConfigUtils
import per.goweii.wanandroid.widget.refresh.SimpleOnMultiListener

class BookFragment : BaseFragment<BookPresenter, FragmentBookBinding>(), BookView {
    private lateinit var mAdapter: BookAdapter

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookBinding {
        return FragmentBookBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): BookPresenter = BookPresenter()

    override fun initView() {
        SmartRefreshUtils.with(binding.srl).pureScrollMode()
        binding.srl.setOnMultiListener(object : SimpleOnMultiListener() {
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
                if (binding.srl.state != RefreshState.PullUpCanceled && isDragging && percent > 1.2F) {
                    binding.srl.closeHeaderOrFooter()
                    CloseSecondFloorEvent().post()
                }
            }
        })
        binding.rv.layoutManager = GridLayoutManager(context, 3)
        mAdapter = BookAdapter()
        RvConfigUtils.init(mAdapter)
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.let { item ->
                BookDetailsActivity.start(requireContext(), item)
            }
        }
        binding.rv.adapter = mAdapter
        MultiStateUtils.setEmptyAndErrorClick(binding.msv) {
            MultiStateUtils.toLoading(binding.msv)
            presenter.getList()
        }
    }

    override fun loadData() {
        MultiStateUtils.toLoading(binding.msv)
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
            MultiStateUtils.toEmpty(binding.msv, true)
        } else {
            MultiStateUtils.toContent(binding.msv)
        }
    }

    override fun getBookListFailed() {
        MultiStateUtils.toError(binding.msv)
    }
}