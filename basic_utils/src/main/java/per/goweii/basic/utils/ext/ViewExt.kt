package per.goweii.basic.utils.ext

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

fun View?.setPadding(px: Int) = this?.run {
    setPadding(px, px, px, px)
}

fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.onPreDraw(block: View.() -> Unit) = this?.run {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.removeOnPreDrawListener(this)
            }
            block.invoke(this@run)
            return true
        }
    })
}

fun View?.measureSize(): IntArray? {
    this?.apply {
        var lp: ViewGroup.LayoutParams? = layoutParams
        if (lp == null) {
            lp = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val lpHeight = lp.height
        val heightSpec: Int
        heightSpec = if (lpHeight > 0) {
            View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
        } else {
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        }
        measure(widthSpec, heightSpec)
        return intArrayOf(measuredWidth, measuredHeight)
    }
    return null
}
