package per.goweii.wanandroid.module.mine.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.FragmentMessageUnreadBinding
import per.goweii.wanandroid.event.MessageDeleteEvent
import per.goweii.wanandroid.event.MessageUpdateEvent
import per.goweii.wanandroid.module.main.model.ListBean
import per.goweii.wanandroid.module.mine.adapter.MessageUnreadAdapter
import per.goweii.wanandroid.module.mine.model.MessageBean
import per.goweii.wanandroid.module.mine.presenter.MessageUnreadPresenter
import per.goweii.wanandroid.module.mine.view.MessageUnreadView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class MessageUnreadFragment : BaseFragment<MessageUnreadPresenter, FragmentMessageUnreadBinding>(), MessageUnreadView {

    companion object {
        const val PAGE_START = 1
        fun create() = MessageUnreadFragment()
    }

    private lateinit var mSmartRefreshUtils: SmartRefreshUtils
    private lateinit var mAdapter: MessageUnreadAdapter

    private var currPage = PAGE_START

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageDeleteEvent(event: MessageDeleteEvent) {
        if (isDetached) return
        mAdapter.data.forEachIndexed { index, notificationBean ->
            if (notificationBean.id == event.messageBean.id) {
                mAdapter.remove(index)
                return@forEachIndexed
            }
        }
        if (mAdapter.data.isEmpty()) {
            currPage = PAGE_START
            presenter.getMessageUnreadList(currPage)
        }
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMessageUnreadBinding {
        return FragmentMessageUnreadBinding.inflate(inflater, container, false)
    }

    override fun initPresenter() = MessageUnreadPresenter()

    override fun initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(binding.srl)
        mSmartRefreshUtils.pureScrollMode()
        mSmartRefreshUtils.setRefreshListener {
            currPage = PAGE_START
            presenter.getMessageUnreadList(currPage)
        }
        binding.rv.layoutManager = LinearLayoutManager(context)
        mAdapter = MessageUnreadAdapter()
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnLoadMoreListener({
            presenter.getMessageUnreadList(currPage)
        }, binding.rv)
        mAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.let {
                UrlOpenUtils.with(it.realLink).open(context)
            }
        }
        binding.rv.adapter = mAdapter
        MultiStateUtils.setEmptyAndErrorClick(binding.msv, SimpleListener {
            MultiStateUtils.toLoading(binding.msv)
            currPage = PAGE_START
            presenter.getMessageUnreadList(currPage)
        })
    }

    override fun loadData() {
    }

    override fun onVisible(isFirstVisible: Boolean) {
        super.onVisible(isFirstVisible)
        if (isFirstVisible) {
            MultiStateUtils.toLoading(binding.msv)
            currPage = PAGE_START
            presenter.getMessageUnreadList(currPage)
        }
    }

    override fun getMessageUnreadListSuccess(code: Int, data: ListBean<MessageBean>) {
        if (data.curPage == PAGE_START) {
            mAdapter.setNewData(data.datas)
            mAdapter.setEnableLoadMore(true)
            if (data.datas == null || data.datas.isEmpty()) {
                MultiStateUtils.toEmpty(binding.msv)
            } else {
                MultiStateUtils.toContent(binding.msv)
            }
        } else {
            mAdapter.addData(data.datas)
            mAdapter.loadMoreComplete()
        }
        if (data.isOver) {
            mAdapter.loadMoreEnd()
        }
        mSmartRefreshUtils.success()
        currPage++
        MessageUpdateEvent().post()
    }

    override fun getMessageUnreadListFail(code: Int, msg: String) {
    }
}