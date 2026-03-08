package per.goweii.wanandroid.module.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.wanandroid.common.Config
import per.goweii.wanandroid.databinding.FragmentExploreBinding
import per.goweii.wanandroid.module.main.activity.ArticleActivity
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.RvScrollTopUtils
import per.goweii.wanandroid.utils.UrlOpenUtils

class ExploreFragment : BaseFragment<ExplorePresenter, FragmentExploreBinding>(), ExploreView {
    private var mSmartRefreshUtils: SmartRefreshUtils? = null
    private var mAdapter: DailyNewsAdapter? = null

    private var lastClickTime = 0L

    override fun initPresenter(): ExplorePresenter {
        return ExplorePresenter()
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentExploreBinding {
        return FragmentExploreBinding.inflate(inflater, container, false);
    }

    override fun initView() {
        binding.abc.titleTextView.setOnClickListener {
            val currClickTime = System.currentTimeMillis()
            if (currClickTime - lastClickTime <= Config.SCROLL_TOP_DOUBLE_CLICK_DELAY) {
                RvScrollTopUtils.smoothScrollTop(binding.rv)
            }
            lastClickTime = currClickTime
        }
        mSmartRefreshUtils = SmartRefreshUtils.with(binding.srl)
        mSmartRefreshUtils!!.pureScrollMode()
        mSmartRefreshUtils!!.setRefreshListener {
            presenter.getDailyNewsFromNet()
        }
        binding.rv.setLayoutManager(LinearLayoutManager(context))
        mAdapter = DailyNewsAdapter()
        mAdapter!!.setEnableLoadMore(false)
        mAdapter!!.setOnItemClickListener { adapter, view, position ->
            val bean = mAdapter?.getItem(position)
            if (bean != null) {
                ArticleActivity.start(requireContext(),
                    bean.url, bean.title,
                    -1, false, "", -1)
//                UrlOpenUtils.with(bean.url).open(requireContext())
            }
        }
        binding.rv.setAdapter(mAdapter)
        MultiStateUtils.setEmptyAndErrorClick(binding.msv) {
            MultiStateUtils.toLoading(binding.msv)
            presenter.getDailyNewsFromNet()
        }
    }

    override fun loadData() {
        MultiStateUtils.toLoading(binding.msv)
        presenter.getDailyNewsFromCache()
    }

    override fun onVisible(isFirstVisible: Boolean) {
        super.onVisible(isFirstVisible)
        if (isFirstVisible) {
            presenter.getDailyNewsFromNet()
        }
    }

    override fun getDailyNewsSuccess(data: List<DailyNewsBean>) {
        mAdapter?.setNewData(data)
        if (data.isEmpty()) {
            MultiStateUtils.toEmpty(binding.msv)
        } else {
            MultiStateUtils.toContent(binding.msv)
        }
        mSmartRefreshUtils?.success()
    }

    override fun getDailyFailed() {
        mSmartRefreshUtils!!.fail()
        MultiStateUtils.toError(binding.msv)
    }
}