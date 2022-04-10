package per.goweii.wanandroid.module.book.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import per.goweii.rxhttp.request.base.BaseBean

@Parcelize
data class BookBean(
    val id: Int,
    val courseId: Int,
    val cover: String,
    val author: String,
    val name: String,
    val desc: String,
    val lisense: String,
    val lisenseLink: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int,
): BaseBean(), Parcelable
