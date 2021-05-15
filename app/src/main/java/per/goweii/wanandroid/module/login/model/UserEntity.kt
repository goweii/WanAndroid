package per.goweii.wanandroid.module.login.model

/**
 * @author CuiZhen
 * @date 2019/5/8
 * GitHub: https://github.com/goweii
 */
data class UserEntity(
        val email: String,
        val username: String,
        val wanid: Int,
        val cmsid: String,
        val jwt: String?,
        val sex: Int,
        val signature: String?,
        val avatar: String?,
        val cover: String?
)