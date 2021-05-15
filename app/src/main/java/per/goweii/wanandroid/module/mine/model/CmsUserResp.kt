package per.goweii.wanandroid.module.mine.model

import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.http.CmsBaseResponse

data class CmsUserResp(
        val id: String,
        val wanid: Int,
        val username: String,
        val password: String,
        val email: String,
        val sex: Int,
        val signature: String,
        val avatar: String?,
        val cover: String?
) : CmsBaseResponse() {

    data class Avatar(
            val id: String,
            val url: String
    ) {
        fun getFullUrl(): String {
            return CmsApi.ApiConfig.BASE_URL + url
        }
    }

    data class Cover(
            val id: String,
            val url: String
    ) {
        fun getFullUrl(): String {
            return CmsApi.ApiConfig.BASE_URL + url
        }
    }
}