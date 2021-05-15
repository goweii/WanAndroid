package per.goweii.wanandroid.module.mine.contract

import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.http.CmsBaseRequest
import per.goweii.wanandroid.module.login.model.UserEntity
import per.goweii.wanandroid.module.mine.model.CmsMineRequest
import per.goweii.wanandroid.utils.UserUtils

/**
 * @author CuiZhen
 * @date 2020/5/27
 */
interface UserInfoView : BaseView {
    fun gotoLogin()
    fun mineInfoSuccess(userEntity: UserEntity)
    fun mineInfoFailed(msg: String)
}

class UserInfoPresenter : BasePresenter<UserInfoView>() {

    fun mineInfo() {
        if (!UserUtils.getInstance().isLogin) {
            if (isAttach) {
                baseView.gotoLogin()
            }
            return
        }
        UserUtils.getInstance().loginUser.let {
            if (isAttach) {
                baseView.mineInfoSuccess(it)
            }
        }
        addToRxLife(CmsMineRequest.mineInfo(CmsBaseRequest.Listener(
                onSuccess = {
                    UserUtils.getInstance().update(it)
                    if (isAttach) {
                        baseView.mineInfoSuccess(UserUtils.getInstance().loginUser)
                    }
                },
                onFailure = {
                    if (isAttach) {
                        baseView.mineInfoFailed(it)
                    }
                }
        )))
    }

    fun updateSex(sex: Int) {
        val jsonObj = JSONObject()
        try {
            jsonObj.put("sex", sex)
        } catch (e: Exception) {
        } finally {
            val body = RequestBody.create(MediaType.parse("application/json"), jsonObj.toString())
            updateInfo(body)
        }
    }

    fun updateSignature(signature: String) {
        val jsonObj = JSONObject()
        try {
            jsonObj.put("signature", signature)
        } catch (e: Exception) {
        } finally {
            val body = RequestBody.create(MediaType.parse("application/json"), jsonObj.toString())
            updateInfo(body)
        }
    }

    fun updateCover(url: String) {
        val jsonObj = JSONObject()
        try {
            jsonObj.put("cover", url)
        } catch (e: Exception) {
        } finally {
            val body = RequestBody.create(MediaType.parse("application/json"), jsonObj.toString())
            updateInfo(body)
        }
    }

    fun updateAvatar(url: String) {
        val jsonObj = JSONObject()
        try {
            jsonObj.put("avatar", url)
        } catch (e: Exception) {
        } finally {
            val body = RequestBody.create(MediaType.parse("application/json"), jsonObj.toString())
            updateInfo(body)
        }
    }

    fun updateEmail(email: String) {
        val jsonObj = JSONObject()
        try {
            jsonObj.put("email", email)
        } catch (e: Exception) {
        } finally {
            val body = RequestBody.create(MediaType.parse("application/json"), jsonObj.toString())
            updateInfo(body)
        }
    }

    private fun updateInfo(body: RequestBody) {
        val userId = UserUtils.getInstance().cmsId
        addToRxLife(CmsMineRequest.updateInfo(userId, body, CmsBaseRequest.Listener(
                onStart = { showLoadingBar() },
                onFinish = { dismissLoadingBar() },
                onSuccess = {
                    UserUtils.getInstance().update(it)
                    if (isAttach) {
                        baseView.mineInfoSuccess(UserUtils.getInstance().loginUser)
                    }
                },
                onFailure = {
                    if (isAttach) {
                        baseView.mineInfoFailed(it)
                    }
                }
        )))
    }
}
