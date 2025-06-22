package per.goweii.basic.core.widget

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * @author CuiZhen
 * @date 2020/4/5
 */
class GrayLayout : FrameLayout {

    companion object {
        fun wrap(activity: Activity) {
            val decorView = activity.window.decorView as ViewGroup
            val decorChild = decorView.getChildAt(0)
            decorView.removeViewInLayout(decorChild)
            val grayLayout = GrayLayout(decorView.context)
            grayLayout.addViewInLayout(decorChild, 0, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            decorView.addView(grayLayout)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val colorMatrix = ColorMatrix().apply {
        setSaturation(0F)
    }

    private val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    private var opened: Boolean = false

    fun isOpen(): Boolean {
        return opened
    }

    fun setOpened(opened: Boolean) {
        this.opened = opened
        if (opened) {
            colorMatrix.setSaturation(0F)
        } else {
            colorMatrix.setSaturation(1F)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.saveLayer(null, paint)
        super.onDraw(canvas)
        canvas.restore()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.saveLayer(null, paint)
        super.dispatchDraw(canvas)
        canvas.restore()
    }
}