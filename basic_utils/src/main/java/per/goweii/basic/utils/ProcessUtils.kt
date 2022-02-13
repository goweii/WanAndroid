package per.goweii.basic.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import java.io.BufferedReader
import java.io.FileReader

@Suppress("ObjectPropertyName")
object ProcessUtils {
    private lateinit var _currentProcessName: String

    val myPid: Int by lazy {
        android.os.Process.myPid()
    }

    val currentProcessName: String by lazy {
        if (this::_currentProcessName.isInitialized) {
            return@lazy _currentProcessName
        }
        var processName: String? = getCurrentProcessNameOnP()
        if (processName.isNullOrBlank()) {
            processName = getCurrentProcessNameByRe()
        }
        if (processName.isNullOrBlank()) {
            processName = getProcessNameByCmdLine(myPid)
        }
        _currentProcessName = processName ?: ""
        _currentProcessName
    }

    fun getCurrentProcessName(context: Context? = null): String {
        if (this::_currentProcessName.isInitialized) {
            return _currentProcessName
        }
        var processName: String? = getCurrentProcessNameOnP()
        if (processName.isNullOrBlank()) {
            processName = getCurrentProcessNameByRe()
        }
        if (processName.isNullOrBlank() && context != null) {
            processName = getProcessName(context, myPid)
        }
        _currentProcessName = processName ?: ""
        return _currentProcessName
    }

    fun getProcessName(context: Context? = null, pid: Int): String? {
        return if (context != null) {
            getProcessNameByAM(context, pid)
        } else {
            getProcessNameByCmdLine(pid)
        }
    }

    private fun getCurrentProcessNameOnP(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }
        return null
    }

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    private fun getCurrentProcessNameByRe(): String? {
        try {
            val clazz = Class.forName("android.app.ActivityThread",
                    false, Application::class.java.classLoader)
            val method = clazz.getDeclaredMethod("currentProcessName")
            method.isAccessible = true
            val obj = method.invoke(null)
            if (obj is String) {
                return obj
            }
        } catch (e: Throwable) {
        }
        return null
    }

    private fun getProcessNameByAM(context: Context, pid: Int): String? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.runningAppProcesses
                ?.find { it.pid == pid }
                ?.processName
    }

    private fun getProcessNameByCmdLine(pid: Int): String? {
        return try {
            BufferedReader(FileReader("/proc/$pid/cmdline"))
                    .use { reader ->
                        val line = reader.readLine()
                        if (!line.isNullOrBlank()) {
                            return@use line.trim { it <= ' ' }
                        }
                        return@use null
                    }
        } catch (e: Throwable) {
            null
        }
    }
}