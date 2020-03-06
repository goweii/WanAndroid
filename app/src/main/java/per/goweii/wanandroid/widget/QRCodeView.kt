package per.goweii.wanandroid.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.TypedValue
import cn.bingoogolapple.qrcode.zxing.ZXingView
import per.goweii.wanandroid.R

/**
 * @author CuiZhen
 * @date 2020/3/6
 */
class QRCodeView : ZXingView {

    private val locationPointColor = context.resources.getColor(R.color.assist)
    private val locationPointRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 15F, context.resources.displayMetrics
    )

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun drawLocationPoints(canvas: Canvas, paint: Paint, ps: Array<out PointF>) {
        if (ps.isEmpty()) return
        var sumx = 0f
        var sumy = 0f
        for (p in ps) {
            sumx += p.x
            sumy += p.y
        }
        val cx = sumx / ps.size
        val cy = sumy / ps.size
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, locationPointRadius, paint)
        paint.color = locationPointColor
        canvas.drawCircle(cx, cy, locationPointRadius * 0.62F, paint)
    }
}