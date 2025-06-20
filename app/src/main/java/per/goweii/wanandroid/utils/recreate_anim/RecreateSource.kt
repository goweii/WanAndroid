package per.goweii.wanandroid.utils.recreate_anim

import android.graphics.Bitmap

internal data class RecreateSource(
    val bitmap: Bitmap,
    val action: Runnable,
) {
    fun recycle() {
        bitmap.recycle()
    }
}