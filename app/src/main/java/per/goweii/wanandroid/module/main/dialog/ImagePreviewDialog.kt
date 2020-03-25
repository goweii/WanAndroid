package per.goweii.wanandroid.module.main.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import per.goweii.actionbarex.common.ActionBarCommon
import per.goweii.anylayer.AnimatorHelper
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.DragLayout
import per.goweii.basic.core.glide.GlideHelper
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.ext.visible
import per.goweii.wanandroid.R
import per.goweii.wanandroid.widget.ImagePreviewView

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ImagePreviewDialog(
        context: Context,
        private val imageUrl: String
) : DialogLayer(context) {

    private var imageMenuDialog: ImageMenuDialog? = null

    init {
        backgroundDimAmount(1F)
        contentView(R.layout.dialog_image_preview)
        contentAnimator(object : AnimatorCreator {
            override fun createInAnimator(target: View): Animator {
                val abc = getView<ActionBarCommon>(R.id.dialog_image_preview_abc)
                val dl = getView<DragLayout>(R.id.dialog_image_preview_dl)
                return AnimatorSet().apply {
                    playTogether(
                            AnimatorHelper.createTopInAnim(abc),
                            AnimatorHelper.createZoomAlphaInAnim(dl)
                    )
                }
            }

            override fun createOutAnimator(target: View): Animator {
                val abc = getView<ActionBarCommon>(R.id.dialog_image_preview_abc)
                val dl = getView<DragLayout>(R.id.dialog_image_preview_dl)
                return AnimatorSet().apply {
                    playTogether(
                            AnimatorHelper.createTopOutAnim(abc),
                            AnimatorHelper.createZoomAlphaOutAnim(dl)
                    )
                }
            }
        })
    }

    private val abc by lazy { getView<ActionBarCommon>(R.id.dialog_image_preview_abc) }
    private val ipv by lazy { getView<ImagePreviewView>(R.id.dialog_image_preview_pv) }
    private val dl by lazy { getView<DragLayout>(R.id.dialog_image_preview_dl) }
    private val tv_tip by lazy { getView<TextView>(R.id.dialog_image_preview_tv_tip) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttach() {
        super.onAttach()
        abc.setOnLeftIconClickListener {
            dismiss()
        }
        ipv.onImagePreviewListener = object : ImagePreviewView.OnImagePreviewListener {
            override fun onTap() {
                dismiss()
            }

            override fun onTouching1() {
                if (ipv.isShown) {
                    val dl = getView<DragLayout>(R.id.dialog_image_preview_dl)
                    dl.setDragStyle(DragLayout.DragStyle.Bottom)
                }
            }

            override fun onTouching2() {
                if (ipv.isShown) {
                    val dl = getView<DragLayout>(R.id.dialog_image_preview_dl)
                    dl.setDragStyle(DragLayout.DragStyle.None)
                }
            }

            override fun onTouchingUp() {
                if (ipv.isShown) {
                    val dl = getView<DragLayout>(R.id.dialog_image_preview_dl)
                    dl.setDragStyle(DragLayout.DragStyle.Bottom)
                }
            }

            override fun onLongClick() {
                imageMenuDialog?.dismiss()
                imageMenuDialog = ImageMenuDialog.show(activity, ipv)
            }
        }
        GlideHelper.with(ipv.context)
                .cache(true)
                .placeHolder(R.drawable.shape_image_perview_place_holder)
                .errorHolder(R.drawable.shape_image_perview_place_holder)
                .load(imageUrl)
                .onProgressListener { progress ->
                    when {
                        progress >= 1F -> {
                            tv_tip.gone()
                            tv_tip.text = "加载成功"
                        }
                        progress < 0F -> {
                            tv_tip.visible()
                            tv_tip.text = "加载失败"
                        }
                        progress == 0F -> {
                            tv_tip.visible()
                            tv_tip.text = "加载中"
                        }
                        else -> {
                            tv_tip.visible()
                            tv_tip.text = "加载中(${(progress * 100).toInt()}%)"
                        }
                    }
                }
                .into(ipv)
        dl.setDragStyle(DragLayout.DragStyle.Bottom)
        dl.setOnDragListener(object : DragLayout.OnDragListener {
            override fun onDragStart() {
                imageMenuDialog?.dismiss()
            }

            override fun onDragging(f: Float) {
                background.alpha = 1F - f
                abc.translationY = -abc.bottom * f
            }

            override fun onDragEnd() {
                dismiss(false)
            }
        })
    }
}