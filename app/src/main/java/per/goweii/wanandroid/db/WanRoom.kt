package per.goweii.wanandroid.db

import androidx.room.Database
import androidx.room.RoomDatabase
import per.goweii.wanandroid.db.dao.ReadLaterDao
import per.goweii.wanandroid.db.dao.ReadRecordDao
import per.goweii.wanandroid.db.model.ReadLaterModel
import per.goweii.wanandroid.db.model.ReadRecordModel

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
@Database(
        entities = [
            ReadLaterModel::class,
            ReadRecordModel::class
        ],
        version = WanRoom.VERSION,
        exportSchema = false,
)
abstract class WanRoom : RoomDatabase() {
    companion object {
        const val NAME = "wan_db"
        const val VERSION = 2
    }

    abstract fun readLaterDao(): ReadLaterDao
    abstract fun readRecordDao(): ReadRecordDao
}