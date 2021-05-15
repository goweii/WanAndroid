package per.goweii.wanandroid.module.mine.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_notification_new.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R
import per.goweii.wanandroid.event.NotificationDeleteEvent
import per.goweii.wanandroid.event.SettingChangeEvent
import per.goweii.wanandroid.module.mine.adapter.NotificationHistoryAdapter
import per.goweii.wanandroid.module.mine.model.NotificationBean
import per.goweii.wanandroid.module.mine.presenter.NotificationHistoryPresenter
import per.goweii.wanandroid.module.mine.view.NotificationHistoryView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.RvConfigUtils
import per.goweii.wanandroid.utils.SettingUtils
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class NotificationHistoryFragment : BaseFragment<NotificationHistoryPresenter>(), NotificationHistoryView {

    companion object {
        const val PAGE_START = 1
        fun create() = NotificationHistoryFragment()
    }

    private lateinit var mSmartRefreshUtils: SmartRefreshUtils
    private lateinit var mAdapter: NotificationHistoryAdapter

    private var currPage = PAGE_START

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSettingChangeEvent(event: SettingChangeEvent) {
        if (isDetached) return
        if (event.isRvAnimChanged) {
            RvConfigUtils.setAnim(mAdapter, SettingUtils.getInstance().rvAnim)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationDeleteEvent(event: NotificationDeleteEvent) {
        if (isDetached) return
        mAdapter.data.forEachIndexed { index, notificationBean ->
            if (notificationBean.deleteUrl == event.notificationBean.deleteUrl) {
                mAdapter.remove(index)
                return@forEachIndexed
            }
        }
        if (mAdapter.data.isEmpty()) {
            currPage = PAGE_START
            presenter.getList(currPage)
        }
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun getLayoutRes() = R.layout.fragment_notification_history

    override fun initPresenter() = NotificationHistoryPresenter()

    override fun initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl)
        mSmartRefreshUtils.pureScrollMode()
        mSmartRefreshUtils.setRefreshListener {
            currPage = PAGE_START
            presenter.getList(currPage)
        }
        rv.layoutManager = LinearLayoutManager(context)
        mAdapter = NotificationHistoryAdapter()
        RvConfigUtils.setAnim(mAdapter, SettingUtils.getInstance().rvAnim)
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnLoadMoreListener({
            presenter.getList(currPage)
        }, rv)
        mAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            mAdapter.closeAll(null)
            val item = mAdapter.getItem(position) ?: return@OnItemChildClickListener
            when (view.id) {
                R.id.rl_notification -> {
                    UrlOpenUtils.with(item.articleUrl).open(context)
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
            presenter.getList(currPage)
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
            presenter.getList(currPage)
        }
    }

    override fun getListSuccess(list: List<NotificationBean>) {
        if (currPage == PAGE_START) {
            mAdapter.setNewData(list)
            mAdapter.setEnableLoadMore(true)
            if (list.isEmpty()) {
                MultiStateUtils.toEmpty(msv)
            } else {
                MultiStateUtils.toContent(msv)
            }
        } else {
            mAdapter.addData(list)
            mAdapter.loadMoreComplete()
            if (list.isEmpty()) {
                mAdapter.loadMoreEnd()
            }
        }
        mSmartRefreshUtils.success()
        currPage++
    }
}