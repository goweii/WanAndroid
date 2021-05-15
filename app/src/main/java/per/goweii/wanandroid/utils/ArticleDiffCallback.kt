package per.goweii.wanandroid.utils

import com.chad.library.adapter.base.diff.BaseQuickDiffCallback
import com.google.gson.Gson
import per.goweii.wanandroid.module.main.model.ArticleBean

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ArticleDiffCallback(
        newList: List<ArticleBean>?
) : BaseQuickDiffCallback<ArticleBean>(newList) {
    private val gson by lazy { Gson() }

    override fun areItemsTheSame(oldItem: ArticleBean, newItem: ArticleBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleBean, newItem: ArticleBean): Boolean {
        return gson.toJson(oldItem) == gson.toJson(newItem)
    }
}