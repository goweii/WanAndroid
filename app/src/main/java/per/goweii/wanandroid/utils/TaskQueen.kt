package per.goweii.wanandroid.utils

/**
 * @author CuiZhen
 * @date 2020/2/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
class TaskQueen {
    private val tasks: MutableList<Task> = mutableListOf()

    fun append(task: Task) {
        if (tasks.isEmpty()) {
            tasks.add(task)
            run(task)
        } else {
            tasks.add(task)
        }
    }

    private fun next() {
        if (tasks.isEmpty()) {
            return
        }
        val task = tasks.removeAt(0)
        run(task)
    }

    private fun run(task: Task) {
        task.onFinished = {
            next()
        }
        task.run()
    }

    abstract class Task {
        internal var onFinished: (() -> Unit)? = null

        abstract fun run()

        fun complete() {
            onFinished?.invoke()
        }
    }
}