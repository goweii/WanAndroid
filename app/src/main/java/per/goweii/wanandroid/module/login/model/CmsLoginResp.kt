package per.goweii.wanandroid.module.login.model

import per.goweii.wanandroid.http.CmsBaseResponse
import per.goweii.wanandroid.module.mine.model.CmsUserResp

/**
 * @author CuiZhen
 * @date 2020/5/24
 */
data class CmsLoginResp(
        val jwt: String,
        val user: CmsUserResp
) : CmsBaseResponse()