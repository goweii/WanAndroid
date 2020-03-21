package per.goweii.wanandroid.db.model

import androidx.room.Entity

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
@Entity(primaryKeys = ["link"])
data class ReadRecordModel(
        val link: String,
        val title: String,
        val time: Long
)