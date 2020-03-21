package per.goweii.wanandroid.db

import android.content.Context
import androidx.room.Room

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
object WanDb {
    private var context: Context? = null
    private var database: WanRoom? = null

    @JvmStatic
    fun init(context: Context) {
        this.context = context.applicationContext
    }

    @JvmStatic
    private fun db(dbName: String): WanRoom {
        database?.run {
            if (isOpen) {
                if (openHelper.databaseName == dbName) {
                    return this
                } else {
                    close()
                }
            }
        }
        database = Room.databaseBuilder(context!!, WanRoom::class.java, dbName).build()
        return database!!
    }

    @JvmStatic
    fun db(): WanRoom {
        return db("wan_db")
    }
}