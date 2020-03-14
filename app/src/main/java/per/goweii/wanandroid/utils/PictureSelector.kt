package per.goweii.wanandroid.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import java.io.File


/**
 * @author CuiZhen
 * @date 2019/6/4
 * GitHub: https://github.com/goweii
 */
class PictureSelector {
    companion object {
        @JvmStatic
        fun select(activity: Activity, requestCode: Int) = apply {
            activity.startActivityForResult(selectIntent(), requestCode)
        }

        @JvmStatic
        fun select(fragment: Fragment, requestCode: Int) = apply {
            fragment.startActivityForResult(selectIntent(), requestCode)
        }

        @JvmStatic
        fun crop(activity: Activity, fileSource: File, fileClip: File, requestCode: Int) = apply {
            activity.startActivityForResult(cropIntent(activity, fileSource, fileClip), requestCode)
        }

        @JvmStatic
        fun crop(fragment: Fragment, fileSource: File, fileClip: File, requestCode: Int) = apply {
            val context = fragment.context ?: return@apply
            fragment.startActivityForResult(cropIntent(context, fileSource, fileClip), requestCode)
        }

        private fun selectIntent(): Intent {
            return Intent(Intent.ACTION_PICK).apply {
                setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
            }
        }

        private fun cropIntent(context: Context, fileSource: File, fileClip: File): Intent {
            return Intent("com.android.camera.action.CROP").apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setDataAndType(getUriFromFile(context, fileSource), "image/*")
                } else {
                    setDataAndType(Uri.fromFile(fileSource), "image/*")
                }
                putExtra("crop", "true")
                putExtra("scale", true)
                putExtra("aspectX", 1)
                putExtra("aspectY", 1)
                putExtra("outputX", 200)
                putExtra("outputY", 200)
                putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileClip))
                putExtra("noFaceDetection", true)
                putExtra("scaleUpIfNeeded", true)
                putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                putExtra("return-data", false)
            }
        }

        @JvmStatic
        fun result(resultCode: Int, data: Intent?): Uri? {
            if (resultCode != Activity.RESULT_OK) return null
            if (null == data) return null
            if (null == data.data) return null
            return data.data
        }

        @JvmStatic
        fun getFileFormUri(context: Context, uri: Uri): File? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            CursorLoader(context, uri, projection, null, null, null).loadInBackground()?.apply {
                getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            }?.use { cursor ->
                val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return File(cursor.getString(index))
            }
            var path = uri.path ?: return null
            return when (uri.scheme) {
                "file" -> {
                    val buff = StringBuffer()
                    buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'$path'").append(")")
                    context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA), buff.toString(), null, null)?.use { cursor ->
                        var dataIdx: Int
                        while (!cursor.isAfterLast) {
                            dataIdx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                            path = cursor.getString(dataIdx)
                            cursor.moveToNext()
                        }
                        File(path)
                    }
                }
                "content" -> {
                    context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            path = cursor.getString(columnIndex)
                        }
                        File(path)
                    }
                }
                else -> {
                    null
                }
            }
        }

        @JvmStatic
        fun getUriFromFile(context: Context, imageFile: File): Uri? {
            val filePath = imageFile.absolutePath
            val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    MediaStore.Images.Media.DATA + "=? ",
                    arrayOf(filePath), null)
            cursor.use { _ ->
                return if (cursor != null && cursor.moveToFirst()) {
                    val id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.MediaColumns._ID))
                    val baseUri = Uri.parse("content://media/external/images/media")
                    Uri.withAppendedPath(baseUri, "" + id)
                } else {
                    if (imageFile.exists()) {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.DATA, filePath)
                        context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    } else {
                        null
                    }
                }
            }
        }
    }
}