package per.goweii.wanandroid.module.main.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.utils.bitmap.BitmapUtils
import per.goweii.wanandroid.module.main.view.ScanView
import kotlin.math.min

/**
 * @author CuiZhen
 * @date 2020/2/26
 */
class ScanPresenter : BasePresenter<ScanView>() {

    fun getBitmapFromPath(path: String): Bitmap? {
        val uri = BitmapUtils.getImageContentUri(context, path)
        uri ?: return null
        val bm = getBitmapFromUri(context, uri)
        bm ?: return null
        return bm
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            parcelFileDescriptor ?: return null
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            val newOpts = BitmapFactory.Options()
            newOpts.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, newOpts)// 此时返回bm为空
            val w = newOpts.outWidth.toFloat()
            val h = newOpts.outHeight.toFloat()
            val s = 720f
            var be = (min(w, h) / s).toInt()
            if (be < 1) be = 1
            newOpts.inSampleSize = be
            newOpts.inJustDecodeBounds = false
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, newOpts)
            parcelFileDescriptor.close()
            return image
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}