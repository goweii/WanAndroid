package per.goweii.wanandroid.module.question.model

import androidx.annotation.IntRange
import per.goweii.rxhttp.core.RxLife
import per.goweii.wanandroid.http.BaseRequest
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.http.WanApi
import per.goweii.wanandroid.http.WanCache
import per.goweii.wanandroid.module.main.model.ArticleListBean

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
object QuestionRequest : BaseRequest() {
    fun getQuestionListCache(@IntRange(from = 1) page: Int,
                             listener: RequestListener<ArticleListBean>) {
        cacheBean(WanCache.CacheKey.QUESTION_LIST(page),
                ArticleListBean::class.java,
                listener)
    }

    fun getQuestionList(rxLife: RxLife,
                        @IntRange(from = 1) page: Int,
                        listener: RequestListener<ArticleListBean>) {
        if (page == 1) {
            netBean(rxLife,
                    WanApi.api().getQuestionList(page),
                    WanCache.CacheKey.QUESTION_LIST(page),
                    listener)
        } else {
            rxLife.add(request(WanApi.api().getQuestionList(page), listener))
        }
    }
}