package per.goweii.wanandroid.utils

class PredefinedTaskQueen {
    private val tasks: MutableList<Task> = mutableListOf()
    private var index = -1

    val isCompleted: Boolean
        get() = index >= tasks.size

    val isRunning: Boolean
        get() = index in 0 until tasks.size

    fun append(task: Task) {
        tasks.forEach {
            if (it.name == task.name) {
                throw IllegalArgumentException("任务名不能重复")
            }
        }
        tasks.add(task)
    }

    fun get(name: String): Task {
        return tasks.find {
            it.name == name
        } ?: throw NullPointerException("任务未添加")
    }

    fun start() {
        index = 0
        executeCurrTask()
    }

    private fun next() {
        index++
        executeCurrTask()
    }

    private fun currTask(): Task? {
        if (index !in 0 until tasks.size) {
            return null
        }
        return tasks[index]
    }

    private fun executeCurrTask() {
        currTask()?.let { task ->
            execute(task)
        }
    }

    private fun execute(task: Task) {
        if (task.isCompleted) {
            next()
        } else {
            task.onCompleted = {
                next()
            }
            task.execute()
        }
    }

    class Task(internal val name: String) : Completion {
        private var runnable: ((Completion) -> Unit)? = null
        internal var onCompleted: (() -> Unit)? = null
        var isRunning = false
            private set
        var isCompleted = false
            private set
        val isRunnable: Boolean
            get() = runnable != null

        internal fun execute() {
            isRunning = true
            runnable?.invoke(this)
        }

        fun runnable(runnable: ((Completion) -> Unit)) {
            this.runnable = runnable
            if (isRunning) {
                runnable.invoke(this)
            }
        }

        override fun complete() {
            if (!isCompleted) {
                isCompleted = true
                onCompleted?.invoke()
            }
        }
    }

    interface Completion {
        fun complete()
    }
}