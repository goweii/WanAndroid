package per.goweii.wanandroid.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.kennyc.view.MultiStateView
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.ext.visible
import per.goweii.basic.utils.listener.OnClickListener2
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R

/**
 * @author CuiZhen
 * @date 2019/5/25
 * GitHub: https://github.com/goweii
 */
class MultiStateUtils {
    companion object {

        @JvmStatic
        fun toLoading(view: MultiStateView) {
            view.viewState = MultiStateView.VIEW_STATE_LOADING
        }

        @JvmStatic
        @JvmOverloads
        fun toEmpty(view: MultiStateView, force: Boolean = false, icon: Int? = R.drawable.ic_empty, text: String? = "什么都木有~") {
            if (force || view.viewState != MultiStateView.VIEW_STATE_CONTENT) {
                view.viewState = MultiStateView.VIEW_STATE_EMPTY
            }
            view.getView(MultiStateView.VIEW_STATE_EMPTY)?.apply {
                val iv = findViewById<ImageView>(R.id.iv_empty)
                if (icon == null || icon <= 0) {
                    iv.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                    iv.gone()
                } else {
                    iv.setImageResource(icon)
                    iv.visible()
                }
                val tv = findViewById<TextView>(R.id.tv_empty)
                if (text.isNullOrEmpty()) {
                    tv.text = ""
                    tv.gone()
                } else {
                    tv.text = text
                    tv.visible()
                }
            }
        }

        @JvmStatic
        @JvmOverloads
        fun toError(view: MultiStateView, force: Boolean = false, icon: Int? = R.drawable.ic_error, text: String? = "怎么又出错~\n小场面，问题不大") {
            if (force || view.viewState != MultiStateView.VIEW_STATE_CONTENT) {
                view.viewState = MultiStateView.VIEW_STATE_ERROR
            }
            view.getView(MultiStateView.VIEW_STATE_ERROR)?.apply {
                val iv = findViewById<ImageView>(R.id.iv_error)
                if (icon == null || icon <= 0) {
                    iv.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                    iv.gone()
                } else {
                    iv.setImageResource(icon)
                    iv.visible()
                }
                val tv = findViewById<TextView>(R.id.tv_error)
                if (text.isNullOrEmpty()) {
                    tv.text = ""
                    tv.gone()
                } else {
                    tv.text = text
                    tv.visible()
                }
            }
        }

        @JvmStatic
        fun toContent(view: MultiStateView) {
            view.viewState = MultiStateView.VIEW_STATE_CONTENT
        }

        @JvmStatic
        fun setEmptyAndErrorClick(view: MultiStateView, listener: SimpleListener) {
            setEmptyClick(view, listener)
            setErrorClick(view, listener)
        }

        @JvmStatic
        fun setEmptyClick(view: MultiStateView, listener: SimpleListener) {
            val empty = view.getView(MultiStateView.VIEW_STATE_EMPTY)
            empty?.setOnClickListener(object : OnClickListener2() {
                override fun onClick2(v: View) {
                    listener.onResult()
                }
            })
        }

        @JvmStatic
        fun setErrorClick(view: MultiStateView, listener: SimpleListener) {
            val error = view.getView(MultiStateView.VIEW_STATE_ERROR)
            error?.setOnClickListener(object : OnClickListener2() {
                override fun onClick2(v: View) {
                    listener.onResult()
                }
            })
        }

    }
}