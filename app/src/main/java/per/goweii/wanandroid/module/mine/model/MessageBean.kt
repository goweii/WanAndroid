package per.goweii.wanandroid.module.mine.model

import android.annotation.SuppressLint
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

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
) {
    val realLink: String
        @SuppressLint("SimpleDateFormat")
        get() = Uri.parse(fullLink)
            .buildUpon()
            .appendQueryParameter("fid", fromUserId.toString())
            .appendQueryParameter("date", SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date(date)))
            .appendQueryParameter("message", message)
            .appendQueryParameter("scrollToKeywords", message
                .substring(0, message.length.coerceAtMost(20))
                .split(Regex("[ ]"))
                .joinToString(","))
            .build()
            .toString()
}