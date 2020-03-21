package per.goweii.wanandroid.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import per.goweii.wanandroid.db.model.ReadRecordModel

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
@Dao
interface ReadRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg mode: ReadRecordModel)

    @Query("DELETE FROM ReadRecordModel WHERE link = :link")
    suspend fun delete(link: String)

    @Query("DELETE FROM ReadRecordModel")
    suspend fun deleteAll()

    @Query("SELECT * FROM ReadRecordModel ORDER BY time DESC LIMIT (:offset), (:count)")
    suspend fun findAll(offset: Int, count: Int): List<ReadRecordModel>

    @Query("""DELETE FROM ReadRecordModel WHERE link NOT IN 
        (SELECT link FROM ReadRecordModel ORDER BY time DESC LIMIT 0, :maxCount)""")
    suspend fun deleteIfMaxCount(maxCount: Int)
}