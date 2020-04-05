package per.goweii.wanandroid.utils

import android.app.Activity
import android.app.Application
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
object GrayFilterHelper : Application.ActivityLifecycleCallbacks {

    private val activitys = arrayListOf<Activity>()

    fun attach(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun refresh() {
        activitys.forEach { refresh(it) }
    }

    private fun refresh(activity: Activity) {
        if (ConfigUtils.getInstance().config.isGrayFilter) {
            enable(activity)
        } else {
            disable(activity)
        }
    }

    private fun enable(activity: Activity) {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        activity.window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    }

    private fun disable(activity: Activity) {
        activity.window.decorView.setLayerType(View.LAYER_TYPE_NONE, null)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activitys.add(activity)
        refresh(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        activitys.remove(activity)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

}