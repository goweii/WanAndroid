package per.goweii.wanandroid.module.login.model

/**
 * @author CuiZhen
 * @date 2020/5/24
 */
data class CmsRegisterBody(
        val wanid: Int,
        val username: String,
        val password: String,
        val email: String
)