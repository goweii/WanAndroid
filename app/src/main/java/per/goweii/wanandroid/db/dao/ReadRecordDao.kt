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

    @Query("UPDATE ReadRecordModel SET lastTime = :lastTime WHERE (link = :link AND lastTime != :lastTime)")
    suspend fun updateLastTime(link: String, lastTime: Long)

    @Query("UPDATE ReadRecordModel SET title = :title WHERE (link = :link AND title != :title)")
    suspend fun updateTitle(link: String, title: String)

    @Query("UPDATE ReadRecordModel SET lastTime = :lastTime, percent = :percent WHERE (link = :link AND percent < :percent)")
    suspend fun updatePercent(link: String, percent: Int, lastTime: Long)

    @Query("SELECT * FROM ReadRecordModel WHERE link = :link")
    suspend fun findByLink(link: String): ReadRecordModel?

    @Query("DELETE FROM ReadRecordModel WHERE link = :link")
    suspend fun delete(link: String)

    @Query("DELETE FROM ReadRecordModel")
    suspend fun deleteAll()

    @Query("SELECT * FROM ReadRecordModel ORDER BY lastTime DESC LIMIT (:offset), (:count)")
    suspend fun findAll(offset: Int, count: Int): List<ReadRecordModel>

    @Query("SELECT * FROM ReadRecordModel WHERE link in (:links) ORDER BY lastTime DESC")
    suspend fun findByLinks(links: List<String>): List<ReadRecordModel>

    @Query("""DELETE FROM ReadRecordModel WHERE link NOT IN 
        (SELECT link FROM ReadRecordModel ORDER BY lastTime DESC LIMIT 0, :maxCount)""")
    suspend fun deleteIfMaxCount(maxCount: Int)
}