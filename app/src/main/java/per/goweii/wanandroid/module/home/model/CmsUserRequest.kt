package per.goweii.wanandroid.module.home.model

import io.reactivex.disposables.Disposable
import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.http.CmsBaseRequest
import per.goweii.wanandroid.module.mine.model.CmsUserResp

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
object CmsUserRequest : CmsBaseRequest() {

    @JvmStatic
    fun userInfo(id: String, listener: Listener<CmsUserResp>): Disposable {
        return request(CmsApi.api().userInfo(id), listener)
    }
}