package per.goweii.wanandroid.widget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Size
import androidx.core.content.getSystemService
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import per.goweii.wanandroid.R

@Suppress("DEPRECATION")
class ParallaxStackLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val sensorManager: SensorManager? = context.getSystemService()
    private val gyroscopeSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    private var sensorEventListener: SensorEventListener? = null

    private var lifecycleObserver: LifecycleObserver? = null
    private var lifecycle: Lifecycle? = null
        set(value) {
            lifecycleObserver?.let { field?.removeObserver(it) }
            field = value
            if (value == null) {
                lifecycleObserver = null
                return
            }
            if (lifecycleObserver == null) {
                lifecycleObserver = LifecycleObserverImpl()
            }
            value.addObserver(lifecycleObserver!!)
        }

    val degrees = FloatArray(3)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findActivity()?.let {
            if (it is LifecycleOwner) {
                lifecycle = it.lifecycle
            }
        }
        registerSensorIfNeeded()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycle = null
        unregisterSensorIfNeeded()
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        registerSensorIfNeeded()
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        unregisterSensorIfNeeded()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (isVisible) {
            registerSensorIfNeeded()
        } else {
            unregisterSensorIfNeeded()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (enabled) {
            registerSensorIfNeeded()
        } else {
            unregisterSensorIfNeeded()
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): FrameLayout.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return when (lp) {
            is LayoutParams -> LayoutParams(lp)
            is FrameLayout.LayoutParams -> LayoutParams(lp)
            is MarginLayoutParams -> LayoutParams(lp)
            else -> LayoutParams(lp)
        }
    }

    override fun generateDefaultLayoutParams(): FrameLayout.LayoutParams {
        return LayoutParams()
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    /**
     * 左手坐标系
     */
    private fun onDegreesChanged(@Size(3) degrees: FloatArray) {
        val degreeX = degrees[0] % 360F
        val degreeY = degrees[1] % 360F
        val degreeZ = degrees[2] % 360F

        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val lp = view.layoutParams as LayoutParams

            if (!lp.isEffective) continue

            view.translationX = if (degreeY in (-90F)..90F) {
                degreeY / 90F * lp.deviationX
            } else {
                view.translationX
            }
            view.translationY = if (degreeX in (-90F)..90F) {
                degreeX / 90F * lp.deviationY
            } else {
                view.translationY
            }
            view.rotation = when {
                lp.rotationZ > 0F -> degreeZ.coerceIn(-lp.rotationZ, lp.rotationZ)
                lp.rotationZ < 0F -> -degreeZ.coerceIn(lp.rotationZ, -lp.rotationZ)
                else -> 0F
            }
            view.rotationX = when {
                lp.rotationX > 0F -> degreeX.coerceIn(-lp.rotationX, lp.rotationX)
                lp.rotationX < 0F -> -degreeX.coerceIn(lp.rotationX, -lp.rotationX)
                else -> 0F
            }
            view.rotationY = when {
                lp.rotationY > 0F -> degreeY.coerceIn(-lp.rotationY, lp.rotationY)
                lp.rotationY < 0F -> -degreeY.coerceIn(lp.rotationY, -lp.rotationY)
                else -> 0F
            }
        }
    }

    private fun registerSensorIfNeeded() {
        if (isEnabled && isAttachedToWindow && isVisible && childCount > 0) {
            if (sensorEventListener == null) {
                sensorEventListener = SensorEventListenerImpl()
                sensorManager?.registerListener(
                        sensorEventListener,
                        gyroscopeSensor,
                        SensorManager.SENSOR_DELAY_UI
                )
            }
        }
    }

    private fun unregisterSensorIfNeeded() {
        if (!isEnabled || !isAttachedToWindow || !isVisible || childCount == 0) {
            if (sensorEventListener != null) {
                sensorManager?.unregisterListener(sensorEventListener)
                sensorEventListener = null
            }
        }
    }

    private fun findActivity(): Activity? {
        fun Context.activity(): Activity? {
            var context = this
            while (true) {
                if (context is Activity) {
                    return context
                }
                if (context is ContextWrapper) {
                    val baseContext = context.baseContext
                    if (baseContext !== context) {
                        context = baseContext
                        continue
                    }
                }
                return null
            }
        }
        return context?.activity()
                ?: rootView?.context?.activity()
    }

    private inner class SensorEventListenerImpl : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            degrees[0] = event.values[1]
            degrees[1] = -event.values[2]
            degrees[2] = event.values[0]
            onDegreesChanged(degrees)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }

    inner class LifecycleObserverImpl : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            isEnabled = true
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            isEnabled = false
        }
    }

    class LayoutParams : FrameLayout.LayoutParams {
        var deviationX = 0F
        var deviationY = 0F
        var rotationX = 0F
        var rotationY = 0F
        var rotationZ = 0F

        val isEffective: Boolean
            get() = deviationX != 0F || deviationY != 0F ||
                    rotationX != 0F || rotationY != 0F || rotationZ != 0F

        constructor() : this(MATCH_PARENT, MATCH_PARENT)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: MarginLayoutParams) : super(source)
        constructor(source: FrameLayout.LayoutParams) : super(source)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            context.obtainStyledAttributes(attrs, R.styleable.ParallaxStackLayout_Layout).use {
                deviationX = it.getDimension(
                        R.styleable.ParallaxStackLayout_Layout_layout_parallaxDeviationX,
                        deviationX
                )
                deviationY = it.getDimension(
                        R.styleable.ParallaxStackLayout_Layout_layout_parallaxDeviationY,
                        deviationY
                )
                rotationX = it.getFloat(
                        R.styleable.ParallaxStackLayout_Layout_layout_parallaxRotationX,
                        rotationX
                )
                rotationY = it.getFloat(
                        R.styleable.ParallaxStackLayout_Layout_layout_parallaxRotationY,
                        rotationY
                )
                rotationZ = it.getFloat(
                        R.styleable.ParallaxStackLayout_Layout_layout_parallaxRotationZ,
                        rotationZ
                )
            }
        }
    }
}