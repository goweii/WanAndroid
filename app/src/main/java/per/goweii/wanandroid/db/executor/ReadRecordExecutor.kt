package per.goweii.wanandroid.db.executor

import androidx.room.withTransaction
import per.goweii.basic.utils.listener.SimpleCallback
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.common.Config
import per.goweii.wanandroid.db.WanDb.db
import per.goweii.wanandroid.db.model.ReadRecordModel

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
class ReadRecordExecutor : DbExecutor() {

    fun findByLinks(
        link: List<String>,
        success: SimpleCallback<List<ReadRecordModel>>,
        error: SimpleCallback<Throwable>
    ) {
        execute({
            db().readRecordDao().findByLinks(link)
        }, {
            success.onResult(it)
        }, {
            error.onResult(it)
        })
    }

    fun add(
        link: String,
        title: String,
        percent: Float,
        success: SimpleCallback<ReadRecordModel>,
        error: SimpleListener
    ) {
        execute({
            val db = db()
            db.withTransaction {
                val dao = db.readRecordDao()
                dao.updateTitle(link, title)
                val old = dao.findByLink(link)
                if (old != null) {
                    val time = System.currentTimeMillis()
                    dao.updateLastTime(link, time)
                    old.copy(lastTime = time)
                } else {
                    val time = System.currentTimeMillis()
                    val model = ReadRecordModel(
                        link, title, time, time,
                        (percent * ReadRecordModel.MAX_PERCENT).toInt()
                    )
                    dao.insert(model)
                    model
                }
            }
        }, {
            success.onResult(it)
            removeIfMaxCount {}
        }, {
            error.onResult()
        })
    }

    fun updateTitle(
        link: String,
        title: String,
        success: SimpleCallback<ReadRecordModel>,
        error: SimpleListener
    ) {
        execute({
            val db = db()
            db.withTransaction {
                val dao = db.readRecordDao()
                dao.updateTitle(link, title)
                dao.findByLink(link)!!
            }
        }, {
            success.onResult(it)
        }, {
            error.onResult()
        })
    }

    fun updatePercent(
        link: String,
        percent: Float,
        lastTime: Long,
        success: SimpleCallback<ReadRecordModel>,
        error: SimpleListener
    ) {
        val p = (percent.coerceIn(0f, 1f) * ReadRecordModel.MAX_PERCENT).toInt()
        execute({
            val db = db()
            db.withTransaction {
                val dao = db.readRecordDao()
                dao.updatePercent(link, p, lastTime)
                dao.findByLink(link)!!
            }
        }, {
            success.onResult(it)
        }, {
            error.onResult()
        })
    }

    fun remove(link: String, success: SimpleListener, error: SimpleListener) {
        execute({
            db().readRecordDao().delete(link)
        }, {
            success.onResult()
        }, {
            error.onResult()
        })
    }

    fun removeAll(success: SimpleListener, error: SimpleListener) {
        execute({
            db().readRecordDao().deleteAll()
        }, {
            success.onResult()
        }, {
            error.onResult()
        })
    }

    fun getList(
        from: Int,
        count: Int,
        success: SimpleCallback<List<ReadRecordModel>>,
        error: SimpleListener
    ) {
        execute({
            db().readRecordDao().findAll(from, count)
        }, {
            success.onResult(it)
        }, {
            error.onResult()
        })
    }

    private fun removeIfMaxCount(finish: () -> Unit) {
        execute({ db().readRecordDao().deleteIfMaxCount(Config.READ_RECORD_MAX_COUNT) }, {
            finish.invoke()
        }, {
            finish.invoke()
        })
    }
}