package per.goweii.wanandroid.db.executor

import kotlinx.coroutines.*

/**
 * @author CuiZhen
 * @date 2020/3/21
 */
open class DbExecutor : CoroutineScope by MainScope() {

    fun destroy() {
        cancel()
    }

    fun <T> execute(runnable: suspend () -> T, success: ((t: T) -> Unit)? = null, error: ((e: Throwable) -> Unit)? = null) {
        launch(CoroutineExceptionHandler { _, _ -> }) {
            val result: T = withContext(Dispatchers.IO) {
                runnable.invoke()
            }
            success?.invoke(result)
        }.invokeOnCompletion {
            it?.let {
                error?.invoke(it)
            }
        }
    }
}