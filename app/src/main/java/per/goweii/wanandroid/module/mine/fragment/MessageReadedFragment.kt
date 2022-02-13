package per.goweii.wanandroid.module.mine.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_message_readed.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R
import per.goweii.wanandroid.event.MessageDeleteEvent
import per.goweii.wanandroid.module.main.model.ListBean
import per.goweii.wanandroid.module.mine.adapter.MessageReadedAdapter
import per.goweii.wanandroid.module.mine.model.MessageBean
import per.goweii.wanandroid.module.mine.presenter.MessageReadedPresenter
import per.goweii.wanandroid.module.mine.view.MessageReadedView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class MessageReadedFragment : BaseFragment<MessageReadedPresenter>(), MessageReadedView {

    companion object {
        const val PAGE_START = 1
        fun create() = MessageReadedFragment()
    }

    private lateinit var mSmartRefreshUtils: SmartRefreshUtils
    private lateinit var mAdapter: MessageReadedAdapter

    private var currPage = PAGE_START

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageDeleteEvent(event: MessageDeleteEvent) {
        if (isDetached) return
        mAdapter.data.forEachIndexed { index, messageBean ->
            if (messageBean.id == event.messageBean.id) {
                mAdapter.remove(index)
                return@forEachIndexed
            }
        }
        if (mAdapter.data.isEmpty()) {
            currPage = PAGE_START
            presenter.getMessageReadList(currPage)
        }
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun getLayoutRes() = R.layout.fragment_message_readed

    override fun initPresenter() = MessageReadedPresenter()

    override fun initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl)
        mSmartRefreshUtils.pureScrollMode()
        mSmartRefreshUtils.setRefreshListener {
            currPage = PAGE_START
            presenter.getMessageReadList(currPage)
        }
        rv.layoutManager = LinearLayoutManager(context)
        mAdapter = MessageReadedAdapter()
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnLoadMoreListener({
            presenter.getMessageReadList(currPage)
        }, rv)
        mAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            mAdapter.closeAll(null)
            val item = mAdapter.getItem(position) ?: return@OnItemChildClickListener
            when (view.id) {
                R.id.rl_message -> {
                    UrlOpenUtils.with(item.fullLink).open(context)
                }
                R.id.tv_delete -> {
                    presenter.delete(item)
                }
            }
        }
        rv.adapter = mAdapter
        MultiStateUtils.setEmptyAndErrorClick(msv, SimpleListener {
            MultiStateUtils.toLoading(msv)
            currPage = PAGE_START
            presenter.getMessageReadList(currPage)
        })
        val parent = rootView?.parent
        if (parent is ViewPager) {
            parent.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
                override fun onPageSelected(i: Int) {}
                override fun onPageScrollStateChanged(i: Int) {
                    if (i != ViewPager.SCROLL_STATE_IDLE) {
                        mAdapter.closeAll(null)
                    }
                }
            })
        }
    }

    override fun loadData() {
    }

    override fun onVisible(isFirstVisible: Boolean) {
        super.onVisible(isFirstVisible)
        if (isFirstVisible) {
            MultiStateUtils.toLoading(msv)
            currPage = PAGE_START
            presenter.getMessageReadList(currPage)
        }
    }

    override fun getMessageReadListSuccess(code: Int, data: ListBean<MessageBean>) {
        if (data.curPage == PAGE_START) {
            mAdapter.setNewData(data.datas)
            mAdapter.setEnableLoadMore(true)
            if (data.datas == null || data.datas.isEmpty()) {
                MultiStateUtils.toEmpty(msv)
            } else {
                MultiStateUtils.toContent(msv)
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
    }

    override fun getMessageReadListFail(code: Int, msg: String) {
    }

    override fun deleteMessageSuccess(code: Int, data: MessageBean) {
        MessageDeleteEvent.post(data)
    }

    override fun deleteMessageFail(code: Int, msg: String) {
    }
}