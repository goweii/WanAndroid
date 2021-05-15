package per.goweii.wanandroid.module.mine.model

import io.reactivex.disposables.Disposable
import okhttp3.RequestBody
import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.http.CmsBaseRequest

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
object CmsMineRequest : CmsBaseRequest() {

    fun mineInfo(listener: Listener<CmsUserResp>): Disposable {
        return request(CmsApi.api().mineInfo(), listener)
    }

    fun updateInfo(
            id: String,
            body: RequestBody,
            listener: Listener<CmsUserResp>
    ): Disposable {
        return request(CmsApi.api().updateInfo(id, body), listener)
    }
}