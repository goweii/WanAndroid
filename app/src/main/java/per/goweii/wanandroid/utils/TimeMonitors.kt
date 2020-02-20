package per.goweii.wanandroid.utils

import per.goweii.basic.utils.TimeMonitor

/**
 * @author CuiZhen
 * @date 2020/2/19
 * GitHub: https://github.com/goweii
 */
enum class TM {
    APP_STARTUP;

    fun start(tag: String) {
        TimeMonitor.start(name, tag)
    }

    fun record(tag: String) {
        TimeMonitor.record(name, tag)
    }

    fun end(tag: String) {
        TimeMonitor.end(name, tag)
    }
}