@file:Suppress("ClassName")

package per.goweii.wanandroid.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration_1_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("ALTER TABLE ReadRecordModel ADD COLUMN percent INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE ReadRecordModel ADD COLUMN lastTime INTEGER NOT NULL DEFAULT 0")
            database.setTransactionSuccessful()
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            database.endTransaction()
        }
    }
}