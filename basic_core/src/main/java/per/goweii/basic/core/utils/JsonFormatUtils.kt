package per.goweii.basic.core.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * @author CuiZhen
 * @date 2020/4/18
 */
object JsonFormatUtils {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun toJson(any: Any?): String {
        return gson.toJson(any)
    }
}