package per.goweii.wanandroid.module.main.presenter

import com.sohu.cyan.android.sdk.api.CyanSdk
import com.sohu.cyan.android.sdk.exception.CyanException
import com.sohu.cyan.android.sdk.http.CyanRequestListener
import com.sohu.cyan.android.sdk.http.response.SubmitResp
import com.sohu.cyan.android.sdk.http.response.TopicCommentsResp
import com.sohu.cyan.android.sdk.http.response.TopicCountResp
import com.sohu.cyan.android.sdk.http.response.TopicLoadResp
import per.goweii.basic.core.base.BasePresenter
import per.goweii.wanandroid.common.Config
import per.goweii.wanandroid.module.main.view.ArticleView

/**
 * @author CuiZhen
 * @date 2020/2/20
 */
class ArticlePresenter : BasePresenter<ArticleView>() {

    private lateinit var cyanSdk: CyanSdk

    var articleUrl: String = ""
    var articleTitle: String = ""
    var articleId: Int = 0
    var collected: Boolean = false
    var userName: String = ""
    var userId: Int = 0

    private var topicId: Long = 0L
    private var pageNo: Int = 0

    override fun attach(baseView: ArticleView) {
        super.attach(baseView)
        cyanSdk = CyanSdk.getInstance(getContext())
    }

    /**
     * 查询单个文章评论数，可实时查询
     */
    fun getCommentCount() {
        cyanSdk.getCommentCount(null, articleUrl, topicId,
                object : CyanRequestListener<TopicCountResp> {
                    override fun onRequestSucceeded(resp: TopicCountResp) {
                        if (isAttach) {
                            baseView.getCommentCountSuccess(resp.count)
                        }
                    }

                    override fun onRequestFailed(e: CyanException) {
                        e.printStackTrace()
                    }
                })
    }

    /**
     * 首次加载文章页面时调用，创建在畅言中的文章信息，加载最新最热列表。
     */
    fun loadTopic() {
        cyanSdk.loadTopic(null, articleUrl, articleTitle, null,
                Config.CYAN_LATEST_SIZE, Config.CYAN_HOT_SIZE, "indent",
                null, 1, Config.CYAN_SUB_SIZE,
                object : CyanRequestListener<TopicLoadResp> {
                    override fun onRequestSucceeded(resp: TopicLoadResp) {
                        topicId = resp.topic_id
                        if (isAttach) {
                            baseView.loadTopicSuccess(resp)
                        }
                    }

                    override fun onRequestFailed(e: CyanException) {
                        e.printStackTrace()
                        if (isAttach) {
                            baseView.loadTopicFail()
                        }
                    }
                })
    }

    /**
     * 分页查询文章评论数据
     */
    fun getTopicComments(pageNo: Int) {
        if (topicId <= 0L) return
        cyanSdk.getTopicComments(topicId, Config.CYAN_PAGE_SIZE, pageNo,
                "indent", null, 1, Config.CYAN_SUB_SIZE,
                object : CyanRequestListener<TopicCommentsResp> {
                    override fun onRequestSucceeded(resp: TopicCommentsResp) {
                        if (isAttach) {
                            baseView.getTopicCommentsSuccess(resp)
                        }
                    }

                    override fun onRequestFailed(e: CyanException) {
                        e.printStackTrace()
                        if (isAttach) {
                            baseView.getTopicCommentsFail()
                        }
                    }
                })
    }

    /**
     * 发表评论接口
     */
    fun submitComment(content: String, replyId: Long) {
        if (topicId <= 0L) return
        if (content.isEmpty()) return
        cyanSdk.submitComment(topicId, content, replyId, null,
                43/*android*/, 0F, null,
                object : CyanRequestListener<SubmitResp> {
                    override fun onRequestSucceeded(resp: SubmitResp) {
                    }

                    override fun onRequestFailed(e: CyanException) {
                        e.printStackTrace()
                    }
                })
    }
}