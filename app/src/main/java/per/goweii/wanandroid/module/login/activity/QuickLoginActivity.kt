package per.goweii.wanandroid.module.login.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.utils.Base64Utils.decodeToBytes
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.swipeback.SwipeBackDirection
import per.goweii.wanandroid.event.LoginEvent
import per.goweii.wanandroid.module.login.model.LoginInfoEntity
import per.goweii.wanandroid.module.login.model.UserEntity
import per.goweii.wanandroid.module.login.presenter.QuickLoginPresenter
import per.goweii.wanandroid.module.login.view.QuickLoginView
import per.goweii.wanandroid.utils.biometric.BiometricHelper

class QuickLoginActivity : BaseActivity<QuickLoginPresenter>(), QuickLoginView, SwipeBackAbility.Direction {
    companion object {
        private const val PARAMS_OPEN_OR_USE = "openOrUse"
        private const val PARAMS_START_PASSWORD_LOGIN_ON_FAIL = "startPasswordLoginOnFail"

        const val PARAMS_LOGIN_INFO = "login_info"
        const val RESULT_CODE_FAILED = RESULT_FIRST_USER + 1

        fun startForOpen(activity: Activity, loginInfoEntity: LoginInfoEntity, reqCode: Int) {
            activity.startActivityForResult(Intent(activity, QuickLoginActivity::class.java).apply {
                putExtra(PARAMS_OPEN_OR_USE, true)
                putExtra(PARAMS_LOGIN_INFO, loginInfoEntity)
            }, reqCode)
            activity.overridePendingTransition(0, 0)
        }

        fun startForUse(activity: Activity, startPasswordLoginOnFail: Boolean, reqCode: Int) {
            activity.startActivityForResult(Intent(activity, QuickLoginActivity::class.java).apply {
                putExtra(PARAMS_OPEN_OR_USE, false)
                putExtra(PARAMS_START_PASSWORD_LOGIN_ON_FAIL, startPasswordLoginOnFail)
            }, reqCode)
            activity.overridePendingTransition(0, 0)
        }

        fun startForUse(context: Context, startPasswordLoginOnFail: Boolean) {
            val activity: Activity? = if (context is Activity) {
                context
            } else {
                if (context is ContextWrapper && context.baseContext is Activity) {
                    context.baseContext as Activity
                } else {
                    null
                }
            }
            context.startActivity(Intent(context, QuickLoginActivity::class.java).apply {
                putExtra(PARAMS_OPEN_OR_USE, false)
                putExtra(PARAMS_START_PASSWORD_LOGIN_ON_FAIL, startPasswordLoginOnFail)
                if (activity == null) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            })
            activity?.overridePendingTransition(0, 0)
        }
    }

    private var openOrUse = false
    private var startPasswordLoginOnFail = false
    private lateinit var biometricHelper: BiometricHelper

    override fun getLayoutId(): Int {
        setContentView(View(this))
        return 0
    }

    override fun initPresenter(): QuickLoginPresenter {
        return QuickLoginPresenter()
    }

    override fun initView() {
        openOrUse = intent.getBooleanExtra(PARAMS_OPEN_OR_USE, openOrUse)
        startPasswordLoginOnFail = intent.getBooleanExtra(PARAMS_START_PASSWORD_LOGIN_ON_FAIL, startPasswordLoginOnFail)
        biometricHelper = BiometricHelper(this)
    }

    override fun loadData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            finishWithFailed()
            return
        }
        if (!biometricHelper.canAuth()) {
            finishWithFailed()
            return
        }
        if (openOrUse) {
            val loginInfoEntity = intent.getSerializableExtra(PARAMS_LOGIN_INFO) as? LoginInfoEntity?
            tryOpenLoginByBiometric(loginInfoEntity)
        } else {
            tryLoginByBiometric()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryOpenLoginByBiometric(loginInfoEntity: LoginInfoEntity?) {
        biometricHelper.onAuthSuccess = { cipher ->
            if (presenter.encodeAndSaveLoginInfo(cipher, loginInfoEntity)) {
                finishWithSuccess()
            } else {
                finishWithFailed()
            }
        }
        biometricHelper.onAuthFailure = {}
        biometricHelper.onAuthError = { _, _ ->
            finishWithFailed()
        }
        biometricHelper.authForEncode("是否开启快捷登录", "暂不开启")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryLoginByBiometric() {
        val loginInfoStr: String = presenter.loginInfo
        val ivStr: String = presenter.cipherIV
        if (TextUtils.isEmpty(loginInfoStr) || TextUtils.isEmpty(ivStr)) {
            finishWithFailed()
            return
        }
        biometricHelper.onAuthSuccess = { cipher ->
            val loginInfoEntity = presenter.decodeLoginInfo(cipher, loginInfoStr)
            if (loginInfoEntity == null || loginInfoEntity.isEmpty) {
                finishWithFailed()
            } else {
                presenter?.login(loginInfoEntity.username, loginInfoEntity.password)
            }
        }
        biometricHelper.onAuthFailure = {}
        biometricHelper.onAuthError = { _, _ ->
            finishWithFailed()
        }
        val ivBytes = decodeToBytes(ivStr)
        biometricHelper.authForDecode(ivBytes, "快捷登录", "密码登录")
    }

    private fun finishWithFailed() {
        if (!openOrUse) {
            if (startPasswordLoginOnFail) {
                AuthActivity.startPasswordLogin(this)
            }
        }
        setResult(RESULT_CODE_FAILED)
        finish()
    }

    private fun finishWithSuccess() {
        setResult(RESULT_OK)
        finish()
    }

    override fun loginSuccess(code: Int, data: UserEntity) {
        LoginEvent(true).post()
        finishWithSuccess()
    }

    override fun loginFailed(code: Int, msg: String?) {
        finishWithFailed()
    }

    override fun swipeBackDirection(): SwipeBackDirection {
        return SwipeBackDirection.NONE
    }
}