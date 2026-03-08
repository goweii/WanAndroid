package per.goweii.wanandroid.module.explore

import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.android.gms.ads.nativead.NativeAd
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter
import per.goweii.wanandroid.module.main.model.NativeAdBean
import per.goweii.wanandroid.module.main.model.ArticleBean

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class DailyNewsDiffCallback(
    newList: List<DailyNewsBean>?
) : BaseQuickDiffCallback<DailyNewsBean>(newList) {
    override fun areItemsTheSame(oldItem: DailyNewsBean, newItem: DailyNewsBean): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: DailyNewsBean, newItem: DailyNewsBean): Boolean {
        return oldItem == newItem
    }
}