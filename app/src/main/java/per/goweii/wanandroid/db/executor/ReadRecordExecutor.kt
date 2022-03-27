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
        val time = System.currentTimeMillis()
        val model = ReadRecordModel(
            link, title, time, time,
            (percent * ReadRecordModel.MAX_PERCENT).toInt()
        )
        execute({
            db().readRecordDao().insert(model)
        }, {
            success.onResult(model)
            removeIfMaxCount {}
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