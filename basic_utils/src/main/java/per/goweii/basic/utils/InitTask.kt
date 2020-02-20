package per.goweii.basic.utils

import android.app.Application
import android.os.AsyncTask
import android.os.Process
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * @author CuiZhen
 */
class InitTaskRunner(private val application: Application) {

    private val mTasks: ArrayList<InitTask> = arrayListOf()

    fun add(task: InitTask): InitTaskRunner {
        mTasks.add(task)
        return this
    }

    fun run() {
        val isMainProcess = isMainProcess()
        val syncTasks: ArrayList<InitTask> = arrayListOf()
        val asyncTasks: ArrayList<InitTask> = arrayListOf()
        for (task in mTasks) {
            if (!isMainProcess && task.onlyMainProcess()) {
                continue
            }
            if (task.sync()) {
                syncTasks.add(task)
            } else {
                asyncTasks.add(task)
            }
        }
        runSync(syncTasks)
        runAsync(asyncTasks)
    }

    private fun runSync(tasks: ArrayList<InitTask>) {
        tasks.sortBy { it.level() }
        for (task in tasks) {
            try {
                task.init(application)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun runAsync(tasks: ArrayList<InitTask>) {
        val tasksMap = hashMapOf<String, ArrayList<InitTask>>()
        for (task in tasks) {
            val name = task.asyncTaskName()
            var list = tasksMap[name]
            if (list == null) {
                list = arrayListOf()
                tasksMap[name] = list
            }
            list.add(task)
        }
        for (map in tasksMap) {
            val task = map.value
            AsyncRunner(application, task).execute()
        }
    }

    private fun isMainProcess(): Boolean {
        return application.packageName == getCurrentProcessName()
    }

    private fun getCurrentProcessName(): String? {
        return try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class AsyncRunner(private val application: Application, private val tasks: ArrayList<InitTask>) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg params: Unit?) {
        tasks.sortBy { it.level() }
        for (task in tasks) {
            try {
                task.init(application)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}

abstract class AsyncInitTask : InitTask {
    override fun sync(): Boolean {
        return false
    }

    override fun level(): Int {
        return 0
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun asyncTaskName(): String {
        return toString()
    }
}

abstract class SyncInitTask : InitTask {
    override fun sync(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun asyncTaskName(): String {
        return ""
    }
}

interface InitTask {
    fun sync(): Boolean
    fun asyncTaskName(): String
    fun level(): Int
    fun onlyMainProcess(): Boolean
    fun init(application: Application)
}