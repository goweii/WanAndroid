package per.goweii.wanandroid.module.main.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import per.goweii.actionbarex.common.ActionBarCommon
import per.goweii.anylayer.AnimatorHelper
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.DragLayout
import per.goweii.wanandroid.R
import per.goweii.wanandroid.utils.ImageLoader
import per.goweii.wanandroid.widget.ImagePreviewView

/**
 * @author CuiZhen
 * @date 2020/3/7
 */
class ImageListPreviewDialog(
        context: Context,
        private val imgs: List<String>,
        private val index: Int
) : DialogLayer(context) {

    private var imageMenuDialog: ImageMenuDialog? = null

    init {
        backgroundDimAmount(1F)
        contentView(R.layout.dialog_image_list_preview)
        contentAnimator(object : AnimatorCreator {
            override fun createInAnimator(target: View): Animator {
                val abc = getView<ActionBarCommon>(R.id.dialog_image_list_preview_abc)
                val vp = getView<androidx.viewpager.widget.ViewPager>(R.id.dialog_image_list_preview_vp)
                return AnimatorSet().apply {
                    playTogether(
                            AnimatorHelper.createTopInAnim(abc),
                            AnimatorHelper.createZoomAlphaInAnim(vp)
                    )
                }
            }

            override fun createOutAnimator(target: View): Animator {
                val abc = getView<ActionBarCommon>(R.id.dialog_image_list_preview_abc)
                val vp = getView<androidx.viewpager.widget.ViewPager>(R.id.dialog_image_list_preview_vp)
                return AnimatorSet().apply {
                    playTogether(
                            AnimatorHelper.createTopOutAnim(abc),
                            AnimatorHelper.createZoomAlphaOutAnim(vp)
                    )
                }
            }
        })
    }

    private val abc by lazy { getView<ActionBarCommon>(R.id.dialog_image_list_preview_abc) }
    private val vp by lazy { getView<androidx.viewpager.widget.ViewPager>(R.id.dialog_image_list_preview_vp) }
    private val dl by lazy { getView<DragLayout>(R.id.dialog_image_list_preview_dl) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttach() {
        super.onAttach()
        abc.setOnLeftIconClickListener {
            dismiss()
        }
        vp.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                when (p0) {
                    androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING -> {
                    }
                    else -> {
                    }
                }
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                dl.setDragStyle(DragLayout.DragStyle.None)
            }

            override fun onPageSelected(p0: Int) {
                dl.setDragStyle(DragLayout.DragStyle.Bottom)
                abc.rightTextView.text = "${p0 + 1}/${imgs.size}"
            }
        })
        vp.adapter = object : androidx.viewpager.widget.PagerAdapter() {
            override fun isViewFromObject(v: View, o: Any): Boolean {
                return v === o
            }

            override fun getCount(): Int {
                return imgs.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val ipv = ImagePreviewView(container.context)
                container.addView(ipv)
                ImageLoader.image(ipv, imgs[position])
                ipv.onImagePreviewListener = object : ImagePreviewView.OnImagePreviewListener {
                    override fun onTap() {
                        dismiss()
                    }

                    override fun onTouching1() {
                        dl.setDragStyle(DragLayout.DragStyle.Bottom)
                    }

                    override fun onTouching2() {
                        dl.setDragStyle(DragLayout.DragStyle.None)
                    }

                    override fun onLongClick() {
                        imageMenuDialog?.dismiss()
                        imageMenuDialog = ImageMenuDialog.show(activity, ipv)
                    }
                }
                return ipv
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
            }
        }
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
        abc.rightTextView.text = "${index + 1}/${imgs.size}"
        vp.setCurrentItem(index, false)
    }
}