package per.goweii.wanandroid.module.mine.model

data class MessageBean(
        val category: Int,
        val date: Long,
        val fromUser: String,
        val fromUserId: Int,
        val fullLink: String,
        val id: Int,
        val isRead: Int,
        val link: String,
        val message: String,
        val niceDate: String,
        val tag: String,
        val title: String,
        val userId: Int
)