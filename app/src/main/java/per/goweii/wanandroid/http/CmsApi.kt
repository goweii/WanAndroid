package per.goweii.wanandroid.http

import io.reactivex.Observable
import okhttp3.RequestBody
import per.goweii.rxhttp.request.Api
import per.goweii.wanandroid.BuildConfig
import per.goweii.wanandroid.module.login.model.CmsLoginBody
import per.goweii.wanandroid.module.login.model.CmsLoginResp
import per.goweii.wanandroid.module.login.model.CmsRegisterBody
import per.goweii.wanandroid.module.main.model.CmsCommentBody
import per.goweii.wanandroid.module.main.model.CmsCommentResp
import per.goweii.wanandroid.module.mine.model.CmsUserResp
import retrofit2.http.*

/**
 * @author CuiZhen
 * @date 2020/5/24
 */
class CmsApi : Api() {

    companion object {
        @JvmStatic
        fun api(): ApiService {
            return api(ApiService::class.java)
        }
    }

    class ApiCode {
        companion object {
            const val SUCCESS = 0
        }
    }

    class ApiConfig {
        companion object {
            const val BASE_URL = BuildConfig.CMS_BASE_URL
        }
    }

    interface ApiService {
        @POST("auth/local/register")
        fun register(@Body body: CmsRegisterBody): Observable<CmsLoginResp>

        @POST("auth/local")
        fun login(@Body body: CmsLoginBody): Observable<CmsLoginResp>

        @POST("comments")
        fun comment(@Body body: CmsCommentBody): Observable<CmsCommentResp>

        @GET("comments")
        fun comments(@Query("articleUrl") articleUrl: String,
                     @Query("_start") offset: Int,
                     @Query("_limit") limit: Int,
                     @Query("_sort") sort: String = "updatedAt:DESC",
                     @QueryMap queryMap: Map<String, String> = emptyMap()
        ): Observable<List<CmsCommentResp>>

        @GET("comments/count")
        fun commentCount(@Query("articleUrl") articleUrl: String): Observable<Int>

        @GET("users/me")
        fun mineInfo(): Observable<CmsUserResp>

        @GET("users/{id}")
        fun userInfo(@Path("id") id: String): Observable<CmsUserResp>

        @PUT("users/{id}")
        fun updateInfo(@Path("id") id: String,
                       @Body body: RequestBody): Observable<CmsUserResp>
    }

}