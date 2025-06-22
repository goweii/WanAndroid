package per.goweii.wanandroid.utils

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
class ArticleDiffCallback(
        newList: List<ArticleBean>?
) : BaseQuickDiffCallback<ArticleBean>(newList) {
    override fun areItemsTheSame(oldItem: ArticleBean, newItem: ArticleBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleBean, newItem: ArticleBean): Boolean {
        return oldItem == newItem
    }
}

class ArticleAndAdDiffCallback(
    newList: List<MultiItemEntity>?
) : BaseQuickDiffCallback<MultiItemEntity>(newList) {
    override fun areItemsTheSame(oldItem: MultiItemEntity, newItem: MultiItemEntity): Boolean {
        if (oldItem.itemType != newItem.itemType) {
            return false
        }
        if (oldItem.itemType == ArticleAdapter.ITEM_TYPE_ARTICLE) {
            oldItem as ArticleBean
            newItem as ArticleBean
            return oldItem.id == newItem.id
        } else if (oldItem.itemType == ArticleAdapter.ITEM_TYPE_AD) {
            oldItem as NativeAdBean
            newItem as NativeAdBean
            return oldItem === newItem
        }
        return false
    }

    override fun areContentsTheSame(oldItem: MultiItemEntity, newItem: MultiItemEntity): Boolean {
        if (oldItem.itemType != newItem.itemType) {
            return false
        }
        if (oldItem.itemType == ArticleAdapter.ITEM_TYPE_ARTICLE) {
            oldItem as ArticleBean
            newItem as ArticleBean
            return oldItem == newItem
        } else if (oldItem.itemType == ArticleAdapter.ITEM_TYPE_AD) {
            oldItem as NativeAdBean
            newItem as NativeAdBean
            return oldItem.nativeAd === newItem.nativeAd
        }
        return false
    }
}