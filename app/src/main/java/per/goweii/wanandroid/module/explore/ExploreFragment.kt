package per.goweii.wanandroid.module.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import per.goweii.basic.core.base.BaseFragment
import per.goweii.wanandroid.databinding.FragmentExploreBinding

class ExploreFragment : BaseFragment<ExplorePresenter, FragmentExploreBinding>(), ExploreView {
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
    }

    override fun loadData() {
    }
}