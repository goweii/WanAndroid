package per.goweii.wanandroid.module.explore

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.ktx.backgroundDimDefault
import per.goweii.anylayer.ktx.contentView
import per.goweii.anylayer.ktx.direction
import per.goweii.anylayer.ktx.horizontal
import per.goweii.anylayer.ktx.onBindData
import per.goweii.anylayer.ktx.vertical
import per.goweii.anylayer.popup.PopupLayer
import per.goweii.basic.core.base.BaseFragment
import per.goweii.basic.core.utils.SmartRefreshUtils
import per.goweii.basic.utils.ResUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.FragmentExploreBinding
import per.goweii.wanandroid.module.main.activity.ArticleActivity
import per.goweii.wanandroid.utils.MultiStateUtils
import per.goweii.wanandroid.utils.SettingUtils

class ExploreFragment : BaseFragment<ExplorePresenter, FragmentExploreBinding>(), ExploreView {
    private var mSmartRefreshUtils: SmartRefreshUtils? = null
    private var mAdapter: DailyNewsAdapter? = null

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
        val arrowDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_down)!!
        arrowDrawable.setTint(ResUtils.getThemeColor(requireContext(), R.attr.colorTextOnMain))
        arrowDrawable.setBounds(0, 0, arrowDrawable.intrinsicWidth, arrowDrawable.intrinsicHeight)
        binding.abc.titleTextView.compoundDrawablePadding =
            ResUtils.getDimens(R.dimen.margin_very_thin).toInt()
        binding.abc.titleTextView.setCompoundDrawablesRelative(null, null, arrowDrawable, null)
        updateTitle()
        binding.abc.titleTextView.setOnClickListener {
            showChoicePlatformDialog()
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
                ArticleActivity.start(
                    requireContext(),
                    bean.url, bean.title,
                    -1, false, "", -1
                )
//                UrlOpenUtils.with(bean.url).open(requireContext())
            }
        }
        binding.rv.setAdapter(mAdapter)
        MultiStateUtils.setEmptyAndErrorClick(binding.msv) {
            MultiStateUtils.toLoading(binding.msv)
            presenter.getDailyNewsFromNet()
        }
    }

    private fun showChoicePlatformDialog() {
        AnyLayer.popup(binding.abc)
            .backgroundDimDefault()
            .direction(PopupLayer.Align.Direction.VERTICAL)
            .horizontal(PopupLayer.Align.Horizontal.CENTER)
            .vertical(PopupLayer.Align.Vertical.BELOW)
            .contentView(R.layout.dialog_choice_daily_news_platform)
            .onBindData layer@{
                val rv = requireView<RecyclerView>(R.id.dialog_choice_daily_news_platform_rv)
                rv.layoutManager = LinearLayoutManager(requireContext())
                rv.adapter = object : BaseQuickAdapter<DailyNewsPlatform, BaseViewHolder>(
                    R.layout.rv_item_choice_daily_news_platform,
                    DailyNewsPlatform.entries.toList().sortedBy { it.platformName }
                ) {
                    override fun convert(holder: BaseViewHolder, item: DailyNewsPlatform) {
                        holder.setText(
                            R.id.rv_item_choice_daily_news_platform_tv_title,
                            item.platformName
                        )
                        holder.setText(
                            R.id.rv_item_choice_daily_news_platform_tv_subtitle,
                            item.contentCategory
                        )
                        holder.setVisible(
                            R.id.rv_item_choice_daily_news_platform_ic_check,
                            item == SettingUtils.getInstance().dailyNewsPlatform
                        )
                        holder.itemView.setOnClickListener {
                            SettingUtils.getInstance().dailyNewsPlatform = item
                            updateTitle()
                            this@layer.dismiss()
                            MultiStateUtils.toLoading(binding.msv)
                            presenter.getDailyNewsFromCache()
                            presenter.getDailyNewsFromNet()
                        }
                    }
                }
            }
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val titleName = requireContext().getString(R.string.today_trending)
        val platformName = SettingUtils.getInstance().dailyNewsPlatform.platformName
        binding.abc.titleTextView.text = "$titleName·$platformName"
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