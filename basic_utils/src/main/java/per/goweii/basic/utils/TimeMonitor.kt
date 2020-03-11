package per.goweii.basic.utils

import android.util.Log

/**
 * 耗时统计
 * start->record*n->end
 *
 * @author CuiZhen
 * @date 2020/2/19
 * GitHub: https://github.com/goweii
 */
object TimeMonitor {
    private val timeMap = mutableMapOf<String, MutableList<TimeCost>>()

    /**
     * 开始一组时间线的记录
     * 同一标记的时间线只能有一条，会移除之前已存在的
     * @param tag4Line 时间线标记
     */
    fun start(tag4Line: String, tag4Start: String = "record start") {
        timeMap.remove(tag4Line)
        timeMap[tag4Line] = mutableListOf()
        record(tag4Line, tag4Start)
    }

    /**
     * 在对应时间线上打点
     * @param tag4Line 时间线标记
     * @param tag4Point 打点标记
     */
    fun record(tag4Line: String, tag4Point: String) {
        timeMap[tag4Line]?.run {
            val time = currTime()
            val totalCost = firstOrNull()?.let {
                time - it.timestamp
            } ?: 0
            val stepCost = lastOrNull()?.let {
                time - it.timestamp
            } ?: 0
            add(TimeCost(tag4Point, time, totalCost, stepCost).also {
                log("$tag4Line:$it")
            })
        }
    }

    /**
     * 结束对应时间线
     * @param tag4Line 时间线标记
     */
    fun end(tag4Line: String, tag4End: String = "record end") {
        record(tag4Line, tag4End)
        timeMap.remove(tag4Line)?.let { list ->
            log(formatTimeLine(tag4Line, list))
        }
    }

    private fun formatTimeLine(tag4Line: String, list: List<TimeCost>): String {
        var l1 = 0
        list.forEach {
            val l = it.tag.length
            if (l > l1) {
                l1 = l
            }
        }
        val l2 = 13
        val l3 = 9
        val l4 = 8
        val sb = StringBuilder()
                .append("one time monitor has ended and all records printed as follows")
                .append("\n┌").append("─".r(n = l1 + l2 + l3 + l4 + 3)).append("┐")
                .append("\n│").append(" ".r(tag4Line, l1 + l2 + l3 + l4 + 3, "")).append("│")
                .append("\n├").append("─".r(n = l1)).append("┬").append("─".r(n = l2)).append("┬").append("─".r(n = l3)).append("┬").append("─".r(n = l4)).append("┤")
                .append("\n│").append(" ".r("tag", l1, "")).append("│").append(" ".r("timestamp", l2, "")).append("│").append(" ".r("totalCost", l3, "")).append("│").append(" ".r("stepCost", l4, "")).append("│")
                .append("\n├").append("─".r(n = l1)).append("┼").append("─".r(n = l2)).append("┼").append("─".r(n = l3)).append("┼").append("─".r(n = l4)).append("┤")
        list.forEach {
            sb.append("\n│").append(" ".r(it.tag, l1, "")).append("│").append(" ".r("", l2, "${it.timestamp}")).append("│").append(" ".r("", l3, "${it.totalCost}")).append("│").append(" ".r("", l4, "${it.stepCost}")).append("│")
        }
        sb.append("\n└").append("─".r(n = l1)).append("┴").append("─".r(n = l2)).append("┴").append("─".r(n = l3)).append("┴").append("─".r(n = l4)).append("┘")
        return sb.toString()
    }

    private fun String.r(b: CharSequence = "", n: Int, e: CharSequence = ""): StringBuilder {
        val sb = StringBuilder(b)
        for (i in sb.length until n - e.length) {
            sb.append(this)
        }
        return sb.append(e)
    }

    private fun log(str: String) {
        if (!DebugUtils.isDebug()) return
        Log.w("TimeMonitor", str)
    }

    private fun currTime(): Long {
        return System.currentTimeMillis()
    }

    data class TimeCost(
            val tag: String,
            val timestamp: Long,
            val totalCost: Long,
            val stepCost: Long
    ) {
        override fun toString(): String {
            return "{tag=$tag,timestamp=$timestamp,totalCost=$totalCost,stepCost=$stepCost}"
        }
    }
}