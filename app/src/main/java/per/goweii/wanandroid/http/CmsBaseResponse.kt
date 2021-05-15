package per.goweii.wanandroid.http

/**
 * @author CuiZhen
 * @date 2020/5/24
 */
abstract class CmsBaseResponse(
        val statusCode: Int = 0,
        val error: String = ""
)