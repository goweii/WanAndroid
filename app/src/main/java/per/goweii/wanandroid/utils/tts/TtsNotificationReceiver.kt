package per.goweii.wanandroid.utils.tts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import per.goweii.basic.utils.LogUtils

@Suppress("FunctionName")
class TtsNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "TtsNotificationReceiver"

        fun ACTION_PLAY(context: Context) = "${context.packageName}.android.ACTION_PLAY"
        fun ACTION_PAUSE(context: Context) = "${context.packageName}.android.ACTION_PAUSE"
        fun ACTION_PREV(context: Context) = "${context.packageName}.android.ACTION_PREV"
        fun ACTION_NEXT(context: Context) = "${context.packageName}.android.ACTION_NEXT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        LogUtils.d(TAG, "onReceive: ${intent.action}")
        if (intent.action == ACTION_PLAY(context)) {
            TtsManager.getInstance(context).resume()
        } else if (intent.action == ACTION_PAUSE(context)) {
            TtsManager.getInstance(context).pause()
        } else if (intent.action == ACTION_PREV(context)) {
            TtsManager.getInstance(context).prev()
        } else if (intent.action == ACTION_NEXT(context)) {
            TtsManager.getInstance(context).next()
        }
    }
}