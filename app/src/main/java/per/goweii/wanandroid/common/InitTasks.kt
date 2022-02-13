package per.goweii.wanandroid.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.CrashHandleCallback
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.WebView
import per.goweii.basic.core.CoreInit
import per.goweii.basic.utils.AsyncInitTask
import per.goweii.basic.utils.DebugUtils
import per.goweii.basic.utils.LogUtils
import per.goweii.basic.utils.SyncInitTask
import per.goweii.basic.utils.listener.SimpleCallback
import per.goweii.burred.Blurred
import per.goweii.ponyo.crash.Crash
import per.goweii.rxhttp.core.RxHttp
import per.goweii.swipeback.SwipeBack
import per.goweii.swipeback.SwipeBackDirection
import per.goweii.wanandroid.BuildConfig
import per.goweii.wanandroid.db.WanDb
import per.goweii.wanandroid.http.RxHttpRequestSetting
import per.goweii.wanandroid.http.WanCache
import per.goweii.wanandroid.module.main.activity.CrashActivity
import per.goweii.wanandroid.utils.*
import per.goweii.wanandroid.utils.web.cache.ReadingModeManager
import per.goweii.wanandroid.widget.refresh.ShiciRefreshHeader
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/2/20
 */

class SmartRefreshInitTask : SyncInitTask() {
    override fun init(application: Application) {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> ShiciRefreshHeader(context) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> ClassicsFooter(context) }
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class CookieManagerInitTask : SyncInitTask() {
    override fun init(application: Application) {
        CookieUtils.init(application)
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class NightModeInitTask : SyncInitTask() {
    override fun init(application: Application) {
        DarkModeUtils.initDarkMode()
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class ThemeInitTask : SyncInitTask(), Application.ActivityLifecycleCallbacks {
    override fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val themeId = ConfigUtils.getInstance().theme
        if (themeId > 0) {
            activity.setTheme(themeId)
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}

class GrayFilterInitTask : SyncInitTask() {
    override fun init(application: Application) {
        GrayFilterHelper.attach(application)
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class SwipeBackInitTask : SyncInitTask() {
    override fun init(application: Application) {
        SwipeBack.getInstance().init(application)
        SwipeBack.getInstance().swipeBackDirection = SwipeBackDirection.RIGHT
        SwipeBack.getInstance().swipeBackTransformer = null
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class RxHttpInitTask : SyncInitTask() {
    override fun init(application: Application) {
        RxHttp.init(application)
        RxHttp.initRequest(RxHttpRequestSetting(CookieUtils.cookieJar))
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class WanDbInitTask : SyncInitTask() {
    override fun init(application: Application) {
        WanDb.init(application)
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class WanCacheInitTask : SyncInitTask() {
    override fun init(application: Application) {
        WanCache.init()
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class CoreInitTask : SyncInitTask() {
    override fun init(application: Application) {
        CoreInit.getInstance().onGoLoginCallback = SimpleCallback { data ->
            UserUtils.getInstance().doIfLogin(data)
        }
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 0
    }
}

class BlurredInitTask : AsyncInitTask() {
    override fun init(application: Application) {
        Blurred.init(application)
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 2
    }
}

class CrashInitTask : SyncInitTask() {
    override fun init(application: Application) {
        Crash.initialize(application)
        Crash.setCrashActivity(CrashActivity::class.java)
    }

    override fun onlyMainProcess(): Boolean {
        return false
    }

    override fun level(): Int {
        return 0
    }
}

class X5InitTask : AsyncInitTask() {
    override fun init(application: Application) {
        QbSdk.initX5Environment(application, object : PreInitCallback {
            override fun onCoreInitFinished() {
                LogUtils.d("x5", "initX5Environment->onCoreInitFinished")
            }

            override fun onViewInitFinished(b: Boolean) {
                LogUtils.d("x5", "initX5Environment->onViewInitFinished=$b")
            }
        })
    }

    override fun onlyMainProcess(): Boolean {
        return false
    }

    override fun level(): Int {
        return 0
    }
}

class ReadingModeTask : AsyncInitTask() {
    override fun init(application: Application) {
        ReadingModeManager.setup()
    }

    override fun onlyMainProcess(): Boolean {
        return true
    }

    override fun level(): Int {
        return 3
    }
}

class BuglyInitTask : SyncInitTask() {
    override fun init(application: Application) {
        if (DebugUtils.isDebug()) return
        CrashReport.setIsDevelopmentDevice(application, DebugUtils.isDebug())
        val strategy = UserStrategy(application)
        strategy.setCrashHandleCallback(object : CrashHandleCallback() {
            override fun onCrashHandleStart(crashType: Int, errorType: String, errorMessage: String, errorStack: String): Map<String, String> {
                val map = LinkedHashMap<String, String>()
                val x5CrashInfo = WebView.getCrashExtraMessage(application)
                map["x5crashInfo"] = x5CrashInfo
                return map
            }

            override fun onCrashHandleStart2GetExtraDatas(crashType: Int, errorType: String, errorMessage: String, errorStack: String): ByteArray? {
                return try {
                    "Extra data.".toByteArray(charset("UTF-8"))
                } catch (e: Exception) {
                    null
                }
            }
        })
        strategy.isUploadProcess = WanApp.isMainProcess()
        CrashReport.initCrashReport(application, BuildConfig.APPID_BUGLY, DebugUtils.isDebug(), strategy)
    }

    override fun onlyMainProcess(): Boolean {
        return false
    }

    override fun level(): Int {
        return 3
    }
}