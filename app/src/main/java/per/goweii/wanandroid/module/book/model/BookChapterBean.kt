package per.goweii.wanandroid.module.book.model

import per.goweii.wanandroid.module.main.model.ArticleBean

data class BookChapterBean(
    val articleBean: ArticleBean,
    var time: Long = 0L,
    var percent: Float = 0F,
)