package per.goweii.wanandroid.db.executor

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

    fun add(link: String, title: String, success: SimpleListener, error: SimpleListener) {
        execute({
            val model = ReadRecordModel(link, title, System.currentTimeMillis())
            db().readRecordDao().insert(model)
        }, {
            success.onResult()
            removeIfMaxCount {}
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

    fun getList(from: Int, count: Int, success: SimpleCallback<List<ReadRecordModel>>, error: SimpleListener) {
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