package per.goweii.wanandroid.module.login.model

import io.reactivex.disposables.Disposable
import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.http.CmsBaseRequest

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
object CmsLoginRequest : CmsBaseRequest() {
    fun register(wanid: Int,
                 username: String,
                 password: String,
                 email: String,
                 listener: Listener<CmsLoginResp>
    ): Disposable {
        val body = CmsRegisterBody(wanid, username, password, email)
        return request(CmsApi.api().register(body), listener)
    }

    fun login(username: String,
              password: String,
              listener: Listener<CmsLoginResp>
    ): Disposable {
        val body = CmsLoginBody(username, password)
        return request(CmsApi.api().login(body), listener)
    }
}