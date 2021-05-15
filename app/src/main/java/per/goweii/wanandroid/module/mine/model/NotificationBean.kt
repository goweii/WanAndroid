package per.goweii.wanandroid.module.mine.model

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
data class NotificationBean(
        val tags: List<String>,
        val aniceDate: String,
        val fromUser: String,
        val articleContent: String,
        val content: String,
        val articleUrl: String,
        val deleteUrl: String
)