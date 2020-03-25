package per.goweii.wanandroid.module.question.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_question.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R
import per.goweii.wanandroid.common.ScrollTop
import per.goweii.wanandroid.event.CollectionEvent
import per.goweii.wanandroid.event.LoginEvent
import per.goweii.wanandroid.event.ScrollTopEvent
import per.goweii.wanandroid.event.SettingChangeEvent
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter
import per.goweii.wanandroid.module.main.model.ArticleBean
import per.goweii.wanandroid.module.main.model.ArticleListBean
import per.goweii.wanandroid.module.question.presenter.QuestionPresenter
import per.goweii.wanandroid.module.question.view.QuestionView
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.MultiStateUtils.Companion.setEmptyAndErrorClick
import per.goweii.wanandroid.utils.RvAnimUtils
import per.goweii.wanandroid.utils.RvScrollTopUtils
import per.goweii.wanandroid.utils.SettingUtils

/**
 * @author CuiZhen
 * @date 2020/3/25
 */
class QuestionFragment : BaseFragment<QuestionPresenter>(), QuestionView, ScrollTop {

    companion object {
        private const val PAGE_START = 1

        @JvmStatic
        fun create(): QuestionFragment {
            return QuestionFragment()
        }
    }

    private lateinit var mSmartRefreshUtils: SmartRefreshUtils
    private lateinit var mAdapter: ArticleAdapter

    private var currPage = PAGE_START

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCollectionEvent(event: CollectionEvent) {
        if (isDetached) {
            return
        }
        if (event.articleId == -1) {
            return
        }
        mAdapter.notifyCollectionEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (isDetached) {
            return
        }
        if (event.isLogin) {
            currPage = PAGE_START
            presenter.getQuestionList(currPage)
        } else {
            mAdapter.notifyAllUnCollect()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSettingChangeEvent(event: SettingChangeEvent) {
        if (isDetached) {
            return
        }
        if (event.isRvAnimChanged) {
            RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().rvAnim)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollTopEvent(event: ScrollTopEvent) {
        if (isAdded && !isDetached) {
            RvScrollTopUtils.smoothScrollTop(rv)
        }
    }

    override fun isRegisterEventBus() = true

    override fun getLayoutRes(): Int = R.layout.fragment_question

    override fun initPresenter(): QuestionPresenter = QuestionPresenter()

    override fun initView() {
        mSmartRefreshUtils = SmartRefreshUtils.with(srl)
        mSmartRefreshUtils.pureScrollMode()
        mSmartRefreshUtils.setRefreshListener {
            currPage = PAGE_START
            presenter.getQuestionList(currPage)
        }
        rv.layoutManager = LinearLayoutManager(context)
        mAdapter = ArticleAdapter()
        RvAnimUtils.setAnim(mAdapter, SettingUtils.getInstance().rvAnim)
        mAdapter.setEnableLoadMore(false)
        mAdapter.setOnLoadMoreListener({
            presenter.getQuestionList(currPage)
        }, rv)
        mAdapter.setOnItemChildViewClickListener { _, v, position ->
            val item: ArticleBean = mAdapter.getArticleBean(position)
            if (v.isChecked) {
                presenter.collect(item, v)
            } else {
                presenter.uncollect(item, v)
            }
        }
        rv.adapter = mAdapter
        setEmptyAndErrorClick(msv, SimpleListener {
            MultiStateUtils.toLoading(msv)
            presenter.getQuestionList(currPage)
        })
    }

    override fun loadData() {
        MultiStateUtils.toLoading(msv)
        presenter.getQuestionListCache(PAGE_START)
    }

    override fun onVisible(isFirstVisible: Boolean) {
        super.onVisible(isFirstVisible)
        if (isFirstVisible) {
            currPage = PAGE_START
            presenter.getQuestionList(currPage)
        }
    }

    override fun scrollTop() {
        if (isAdded && !isDetached) {
            RvScrollTopUtils.smoothScrollTop(rv)
        }
    }

    override fun getQuestionListSuccess(code: Int, data: ArticleListBean) {
        currPage = data.curPage + PAGE_START
        if (data.curPage == PAGE_START) {
            MultiStateUtils.toContent(msv)
            mAdapter.setNewData(data.datas)
        } else {
            mAdapter.addData(data.datas)
            mAdapter.loadMoreComplete()
        }
        if (data.isOver) {
            mAdapter.loadMoreEnd()
        } else {
            if (!mAdapter.isLoadMoreEnable) {
                mAdapter.setEnableLoadMore(true)
            }
        }
        mSmartRefreshUtils.success()
    }

    override fun getQuestionListFail(code: Int, msg: String) {
        ToastMaker.showShort(msg)
        mSmartRefreshUtils.fail()
        mAdapter.loadMoreFail()
    }
}