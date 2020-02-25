package per.goweii.wanandroid.module.main.view

import com.sohu.cyan.android.sdk.http.response.TopicCommentsResp
import com.sohu.cyan.android.sdk.http.response.TopicLoadResp
import per.goweii.basic.core.base.BaseView

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
interface ArticleView : BaseView {
    fun getCommentCountSuccess(count: Int)
    fun loadTopicSuccess(resp: TopicLoadResp)
    fun loadTopicFail()
    fun getTopicCommentsSuccess(resp: TopicCommentsResp)
    fun getTopicCommentsFail()
}