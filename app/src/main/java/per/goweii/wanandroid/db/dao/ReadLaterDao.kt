package per.goweii.wanandroid.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import per.goweii.wanandroid.db.model.ReadLaterModel

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
@Dao
interface ReadLaterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg mode: ReadLaterModel)

    @Query("DELETE FROM ReadLaterModel WHERE link = :link")
    suspend fun delete(link: String)

    @Query("DELETE FROM ReadLaterModel")
    suspend fun deleteAll()

    @Query("SELECT * FROM ReadLaterModel ORDER BY time DESC LIMIT (:offset), (:count)")
    suspend fun findAll(offset: Int, count: Int): List<ReadLaterModel>
}