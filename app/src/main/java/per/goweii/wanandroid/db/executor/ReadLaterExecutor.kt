package per.goweii.wanandroid.db.executor

import per.goweii.basic.utils.listener.SimpleCallback
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.db.WanDb.db
import per.goweii.wanandroid.db.model.ReadLaterModel

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
class ReadLaterExecutor : DbExecutor() {

    fun add(link: String, title: String, success: SimpleListener, error: SimpleListener) {
        execute({
            val model = ReadLaterModel(link, title, System.currentTimeMillis())
            db().readLaterDao().insert(model)
        }, {
            success.onResult()
        }, {
            error.onResult()
        })
    }

    fun remove(link: String, success: SimpleListener, error: SimpleListener) {
        execute({
            db().readLaterDao().delete(link)
        }, {
            success.onResult()
        }, {
            error.onResult()
        })
    }

    fun removeAll(success: SimpleListener, error: SimpleListener) {
        execute({
            db().readLaterDao().deleteAll()
        }, {
            success.onResult()
        }, {
            error.onResult()
        })
    }

    fun getList(from: Int, count: Int, success: SimpleCallback<List<ReadLaterModel>>, error: SimpleListener) {
        execute({
            db().readLaterDao().findAll(from, count)
        }, {
            success.onResult(it)
        }, {
            error.onResult()
        })
    }
}