package per.goweii.wanandroid.utils.recreate_anim

import android.app.Activity

class RecreateAnimation(
    private val activity: Activity,
    private val trigger: Runnable,
) {
    fun start() {
        RecreateAnimActivity.launch(activity, trigger)
    }
}