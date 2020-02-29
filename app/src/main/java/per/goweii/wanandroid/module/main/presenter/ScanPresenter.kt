package per.goweii.wanandroid.module.main.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.king.zxing.DecodeFormatManager
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.utils.bitmap.BitmapUtils
import per.goweii.wanandroid.module.main.view.ScanView
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/2/26
 */
class ScanPresenter : BasePresenter<ScanView>() {

    fun parseCode(path: String): String? {
        val uri = BitmapUtils.getImageContentUri(context, path)
        uri ?: return null
        val bm = getBitmapFromUri(context, uri)
        bm ?: return null
        val decodeFormats = Vector<BarcodeFormat>()
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS)
        val hints: MutableMap<DecodeHintType, Any> = mutableMapOf()
        hints[DecodeHintType.TRY_HARDER] = true
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        var result: Result? = null
        try {
            val reader = MultiFormatReader()
            reader.setHints(hints)
            val source = getRGBLuminanceSource(bm)
            source ?: return null
            var isReDecode: kotlin.Boolean
            try {
                val bitmap = BinaryBitmap(HybridBinarizer(source))
                result = reader.decodeWithState(bitmap)
                isReDecode = false
            } catch (e: Exception) {
                isReDecode = true
            }
            if (isReDecode) {
                try {
                    val bitmap = BinaryBitmap(HybridBinarizer(source.invert()))
                    result = reader.decodeWithState(bitmap)
                    isReDecode = false
                } catch (e: Exception) {
                    isReDecode = true
                }
            }
            if (isReDecode) {
                try {
                    val bitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                    result = reader.decodeWithState(bitmap)
                    isReDecode = false
                } catch (e: Exception) {
                    isReDecode = true
                }
            }
            if (isReDecode && source.isRotateSupported) {
                try {
                    val bitmap = BinaryBitmap(HybridBinarizer(source.rotateCounterClockwise()))
                    result = reader.decodeWithState(bitmap)
                } catch (e: Exception) {
                }
            }
            reader.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (!bm.isRecycled) {
                bm.recycle()
            }
        }
        result ?: return null
        return result.text
    }


    /**
     * 获取RGBLuminanceSource
     * @param bitmap
     * @return
     */
    private fun getRGBLuminanceSource(bitmap: Bitmap): RGBLuminanceSource? {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return RGBLuminanceSource(width, height, pixels)
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            parcelFileDescriptor ?: return null
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            val newOpts = BitmapFactory.Options()
            newOpts.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, newOpts)// 此时返回bm为空
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            val width = 800f
            val height = 480f
            var be = 1
            if (w > h && w > width) {
                be = (newOpts.outWidth / width).toInt()
            } else if (w < h && h > height) {
                be = (newOpts.outHeight / height).toInt()
            }
            if (be <= 0) be = 1
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