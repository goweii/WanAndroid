package per.goweii.wanandroid.module.mine.fragment

import androidx.recyclerview.widget.LinearLayoutManager
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
import per.goweii.wanandroid.module.mine.adapter.NotificationNewAdapter
import per.goweii.wanandroid.module.mine.model.NotificationBean
import per.goweii.wanandroid.module.mine.presenter.NotificationNewPresenter
import per.goweii.wanandroid.module.mine.view.NotificationNewView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.RvConfigUtils
import per.goweii.wanandroid.utils.SettingUtils
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class NotificationNewFragment : BaseFragment<NotificationNewPresenter>(), NotificationNewView {

    companion object {
        const val PAGE_START = 1
        fun create() = NotificationNewFragment()
    }

    private lateinit var mSmartRefreshUtils: SmartRefreshUtils
    private lateinit var mAdapter: NotificationNewAdapter

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

    override fun getLayoutRes() = R.layout.fragment_notification_new

    override fun initPresenter() = NotificationNewPresenter()

    override fun initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl)
        mSmartRefreshUtils.pureScrollMode()
        mSmartRefreshUtils.setRefreshListener {
            currPage = PAGE_START
            presenter.getList(currPage)
        }
        rv.layoutManager = LinearLayoutManager(context)
        mAdapter = NotificationNewAdapter()
        RvConfigUtils.setAnim(mAdapter, SettingUtils.getInstance().rvAnim)
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnLoadMoreListener({
            presenter.getList(currPage)
        }, rv)
        mAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.let {
                UrlOpenUtils.with(it.articleUrl).open(context)
            }
        }
        rv.adapter = mAdapter
        MultiStateUtils.setEmptyAndErrorClick(msv, SimpleListener {
            MultiStateUtils.toLoading(msv)
            currPage = PAGE_START
            presenter.getList(currPage)
        })
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
                MultiStateUtils.toEmpty(msv, true)
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