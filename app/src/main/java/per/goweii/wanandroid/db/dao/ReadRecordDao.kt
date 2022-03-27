package per.goweii.wanandroid.db.dao

import androidx.room.*
import per.goweii.wanandroid.db.model.ReadRecordModel

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
@Dao
interface ReadRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg mode: ReadRecordModel)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(vararg mode: ReadRecordModel)

    @Query("UPDATE ReadRecordModel SET lastTime = :lastTime, percent = :percent WHERE (link = :link AND percent < :percent)")
    suspend fun updatePercent(link: String, percent: Int, lastTime: Long)

    @Query("SELECT * FROM ReadRecordModel WHERE link = :link")
    suspend fun findByLink(link: String): ReadRecordModel?

    @Query("DELETE FROM ReadRecordModel WHERE link = :link")
    suspend fun delete(link: String)

    @Query("DELETE FROM ReadRecordModel")
    suspend fun deleteAll()

    @Query("SELECT * FROM ReadRecordModel ORDER BY time DESC LIMIT (:offset), (:count)")
    suspend fun findAll(offset: Int, count: Int): List<ReadRecordModel>

    @Query("SELECT * FROM ReadRecordModel WHERE link in (:links)")
    suspend fun findByLinks(links: List<String>): List<ReadRecordModel>

    @Query("""DELETE FROM ReadRecordModel WHERE link NOT IN 
        (SELECT link FROM ReadRecordModel ORDER BY time DESC LIMIT 0, :maxCount)""")
    suspend fun deleteIfMaxCount(maxCount: Int)
}