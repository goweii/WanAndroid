package per.goweii.wanandroid.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import per.goweii.wanandroid.R
import kotlin.math.min
import kotlin.random.Random

/**
 * @author CuiZhen
 * @date 2020/3/14
 */
class LogoAnimView : FrameLayout, Runnable {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val logo: ImageView = ImageView(context).apply {
        setImageResource(R.drawable.logo)
        scaleType = ImageView.ScaleType.FIT_CENTER
    }
    private val eyeLeft: ImageView = ImageView(context).apply {
        setImageResource(R.drawable.bg_white_circle)
        scaleType = ImageView.ScaleType.FIT_CENTER
    }
    private val eyeRight: ImageView = ImageView(context).apply {
        setImageResource(R.drawable.bg_white_circle)
        scaleType = ImageView.ScaleType.FIT_CENTER
    }

    init {
        addViewInLayout(logo, childCount, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        })
        addViewInLayout(eyeLeft, childCount, LayoutParams(0, 0).apply {
            gravity = Gravity.LEFT
        })
        addViewInLayout(eyeRight, childCount, LayoutParams(0, 0).apply {
            gravity = Gravity.RIGHT
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(w, h)
        setMeasuredDimension(size, size)
        val eyeSize = (size * 0.1F).toInt()
        eyeLeft.layoutParams.apply {
            this as LayoutParams
            width = eyeSize
            height = eyeSize
            leftMargin = (size * 0.23F).toInt()
            topMargin = (size * 0.45F).toInt()
        }
        eyeRight.layoutParams.apply {
            this as LayoutParams
            width = eyeSize
            height = eyeSize
            rightMargin = (size * 0.23F).toInt()
            topMargin = (size * 0.45F).toInt()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        eyeAnim?.cancel()
    }

    private var eyeAnim: Animator? = null

    fun close(onEnd: (() -> Unit)? = null) {
        removeCallbacks(this)
        AnimatorSet().apply {
            playTogether(
                    ObjectAnimator.ofFloat(eyeLeft, "translationY", eyeLeft.translationY, eyeLeft.height.toFloat()),
                    ObjectAnimator.ofFloat(eyeRight, "translationY", eyeRight.translationY, eyeRight.height.toFloat())
            )
            interpolator = DecelerateInterpolator()
            duration = 200
            onEnd(onEnd)
        }.also {
            eyeAnim?.cancel()
            eyeAnim = it
        }.start()
    }

    fun open(onEnd: (() -> Unit)? = null) {
        removeCallbacks(this)
        AnimatorSet().apply {
            playTogether(
                    ObjectAnimator.ofFloat(eyeLeft, "translationY", eyeLeft.translationY, 0F),
                    ObjectAnimator.ofFloat(eyeRight, "translationY", eyeRight.translationY, 0F)
            )
            interpolator = AccelerateInterpolator()
            duration = 200
            onEnd(onEnd)
        }.also {
            eyeAnim?.cancel()
            eyeAnim = it
        }.start()
    }

    fun blink(times: Int, onEnd: (() -> Unit)? = null) {
        removeCallbacks(this)
        AnimatorSet().apply {
            playTogether(
                    ObjectAnimator.ofFloat(eyeLeft, "translationY", 0F, eyeLeft.height.toFloat(), 0F).apply {
                        repeatCount = times
                    },
                    ObjectAnimator.ofFloat(eyeRight, "translationY", 0F, eyeRight.height.toFloat(), 0F).apply {
                        repeatCount = times
                    }
            )
            interpolator = AccelerateInterpolator()
            duration = 400
            onEnd(onEnd)
        }.also {
            eyeAnim?.cancel()
            eyeAnim = it
        }.start()
    }

    fun randomBlink() {
        postDelayed(this, Random.nextLong(1000L) + 2000L)
    }

    override fun run() {
        blink(Random.nextInt(2)) {
            randomBlink()
        }
    }

    private fun Animator.onEnd(onEnd: (() -> Unit)? = null) {
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd?.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }
}