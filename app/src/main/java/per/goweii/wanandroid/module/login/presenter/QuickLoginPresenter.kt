package per.goweii.wanandroid.module.login.presenter

import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.utils.Base64Utils.decodeToBytes
import per.goweii.basic.utils.Base64Utils.decodeToString
import per.goweii.basic.utils.Base64Utils.encodeToBytes
import per.goweii.basic.utils.Base64Utils.encodeToString
import per.goweii.basic.utils.SPUtils
import per.goweii.rxhttp.request.exception.ExceptionHandle
import per.goweii.wanandroid.http.CmsBaseRequest
import per.goweii.wanandroid.http.RequestListener
import per.goweii.wanandroid.module.login.model.CmsLoginRequest.login
import per.goweii.wanandroid.module.login.model.LoginBean
import per.goweii.wanandroid.module.login.model.LoginInfoEntity
import per.goweii.wanandroid.module.login.model.LoginRequest
import per.goweii.wanandroid.module.login.view.QuickLoginView
import per.goweii.wanandroid.utils.UserUtils
import javax.crypto.Cipher

class QuickLoginPresenter : BasePresenter<QuickLoginView>() {
    private val spUtils = SPUtils.newInstance("authInfo")

    val loginInfo: String
        get() = spUtils.get("loginInfo", "")
    val cipherIV: String
        get() = spUtils.get("cipherIV", "")

    fun encodeAndSaveLoginInfo(cipher: Cipher, loginInfoEntity: LoginInfoEntity?): Boolean {
        return if (loginInfoEntity == null || loginInfoEntity.isEmpty) {
            false
        } else try {
            val loginInfoStr = loginInfoEntity.toJson()
            val loginInfoBytes = encodeToBytes(loginInfoStr)
            val loginInfoEncodeBytes = cipher.doFinal(loginInfoBytes)
            val loginInfoEncodeStr = encodeToString(loginInfoEncodeBytes)
            val ivBytes = cipher.iv
            val ivStr = encodeToString(ivBytes)
            spUtils.save(SP_KEY_LOGIN_INFO, loginInfoEncodeStr)
            spUtils.save(SP_KEY_CIPHER_IV, ivStr)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun decodeLoginInfo(cipher: Cipher, loginInfoStr: String?): LoginInfoEntity? {
        return try {
            val loginInfoBytes = decodeToBytes(loginInfoStr)
            val loginInfoDecodedBytes = cipher.doFinal(loginInfoBytes)
            val loginInfoJson = decodeToString(loginInfoDecodedBytes)
            LoginInfoEntity.fromJson(loginInfoJson)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun login(username: String, password: String) {
        addToRxLife(LoginRequest.login(username, password, object : RequestListener<LoginBean> {
            override fun onStart() {
                showLoadingDialog()
            }

            override fun onSuccess(code: Int, data: LoginBean) {
                cmsLogin(username, password)
            }

            override fun onFailed(code: Int, msg: String) {
                UserUtils.getInstance().logout()
                dismissLoadingDialog()
                if (isAttach) {
                    baseView.loginFailed(code, msg)
                }
            }

            override fun onError(handle: ExceptionHandle) {}
            override fun onFinish() {}
        }))
    }

    private fun cmsLogin(username: String, password: String) {
        addToRxLife(login(username, password, CmsBaseRequest.Listener(null, null, {
            UserUtils.getInstance().login(it)
            dismissLoadingDialog()
            if (it.user.wanid > 0) {
                if (isAttach) {
                    baseView.loginSuccess(0, UserUtils.getInstance().loginUser)
                }
            } else {
                if (isAttach) {
                    baseView.loginFailed(0, "")
                }
            }
        }, {
            UserUtils.getInstance().logout()
            dismissLoadingDialog()
            if (isAttach) {
                baseView.loginFailed(0, it)
            }
        })))
    }

    companion object {
        private const val SP_KEY_LOGIN_INFO = "loginInfo"
        private const val SP_KEY_CIPHER_IV = "cipherIV"
    }
}