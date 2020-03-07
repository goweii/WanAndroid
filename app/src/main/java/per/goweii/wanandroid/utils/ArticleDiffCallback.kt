package per.goweii.wanandroid.utils

import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter
import per.goweii.wanandroid.module.main.model.ArticleBean

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ArticleDiffCallback(
        newList: List<MultiItemEntity>?
) : BaseQuickDiffCallback<MultiItemEntity>(newList) {
    private val gson by lazy { Gson() }

    override fun areItemsTheSame(oldItem: MultiItemEntity, newItem: MultiItemEntity): Boolean {
        return if (oldItem.itemType == newItem.itemType) {
            when (oldItem.itemType) {
                ArticleAdapter.ITEM_TYPE_ARTICLE -> {
                    oldItem as ArticleBean
                    newItem as ArticleBean
                    oldItem.id == newItem.id
                }
                else -> false
            }
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItem: MultiItemEntity, newItem: MultiItemEntity): Boolean {
        return if (oldItem.itemType == newItem.itemType) {
            when (oldItem.itemType) {
                ArticleAdapter.ITEM_TYPE_ARTICLE -> {
                    oldItem as ArticleBean
                    newItem as ArticleBean
                    gson.toJson(oldItem) == gson.toJson(newItem)
                }
                else -> false
            }
        } else {
            false
        }
    }
}