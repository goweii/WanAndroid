package per.goweii.wanandroid.module.main.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class BottomDrawerViewOutlineProvider(
        private val radius: Float
) : ViewOutlineProvider() {
    private var view: View? = null
    private var faction: Float = 0F

    fun setToView(v: View) {
        v.clipToOutline = true
        v.outlineProvider = this
        view = v
    }

    fun updateFaction(f: Float) {
        faction = f
        view?.invalidateOutline()
    }

    override fun getOutline(v: View, outline: Outline) {
        view = v
        val r = radius * faction
        outline.setRoundRect(0, 0, v.width, v.height + (r + 0.5F).toInt(), r)
    }
}