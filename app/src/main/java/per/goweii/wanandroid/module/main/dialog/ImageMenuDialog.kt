package per.goweii.wanandroid.module.main.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.widget.TextView
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.github.chrisbanes.photoview.PhotoView
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.DragLayout
import per.goweii.anypermission.RequestListener
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.bitmap.BitmapUtils
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.ext.visible
import per.goweii.wanandroid.R
import per.goweii.wanandroid.utils.UrlOpenUtils
import kotlin.math.max

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ImageMenuDialog(
        private val context: Context,
        private val bitmap: Bitmap,
        private val qrcode: String?
) : DialogLayer(context) {

    companion object {
        fun show(context: Context, iv: PhotoView): ImageMenuDialog? {
            try {
                val drawable = iv.drawable ?: return null
                val bd = drawable as BitmapDrawable
                val bitmap = bd.bitmap ?: return null
                val scale: Float = max(720F / bitmap.width.toFloat(), 720F / bitmap.height.toFloat())
                return if (scale < 1F) {
                    val w = (bitmap.width * scale).toInt()
                    val h = (bitmap.height * scale).toInt()
                    val bitmapScaled = Bitmap.createScaledBitmap(bitmap, w, h, false)
                    val qrcode = QRCodeDecoder.syncDecodeQRCode(bitmapScaled)
                    bitmapScaled.recycle()
                    ImageMenuDialog(context, bitmap, qrcode).apply {
                        show()
                    }
                } else {
                    val qrcode = QRCodeDecoder.syncDecodeQRCode(bitmap)
                    ImageMenuDialog(context, bitmap, qrcode).apply {
                        show()
                    }
                }
            } catch (e: Exception) {
            }
            return null
        }
    }

    init {
        backgroundDimAmount(0.3F)
        contentView(R.layout.dialog_image_menu)
        dragDismiss(DragLayout.DragStyle.Bottom)
        gravity(Gravity.BOTTOM)
        onClickToDismiss(R.id.dialog_image_menu_iv_dismiss)
    }

    private val tv_save by lazy { getView<TextView>(R.id.dialog_image_menu_tv_save) }
    private val tv_qrcode by lazy { getView<TextView>(R.id.dialog_image_menu_tv_qrcode) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttach() {
        super.onAttach()
        if (qrcode.isNullOrEmpty()) {
            tv_qrcode.gone()
        } else {
            tv_qrcode.visible()
            tv_qrcode.setOnClickListener {
                UrlOpenUtils.with(qrcode).open(context)
                dismiss()
            }
        }
        tv_save.setOnClickListener {
            saveBitmap(bitmap)
            dismiss()
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        PermissionUtils.request(object : RequestListener {
            override fun onSuccess() {
                if (null != BitmapUtils.saveGallery(bitmap, "wanandroid_article_image_${System.currentTimeMillis()}")) {
                    ToastMaker.showShort("以保存到相册")
                } else {
                    ToastMaker.showShort("保存失败")
                }
            }

            override fun onFailed() {}
        }, context, 0, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}