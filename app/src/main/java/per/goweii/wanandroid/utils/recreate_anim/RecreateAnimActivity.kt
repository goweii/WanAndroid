package per.goweii.wanandroid.utils.recreate_anim

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.swipeback.SwipeBackAbility

class RecreateAnimActivity : AppCompatActivity(), SwipeBackAbility.Direction {
    companion object {
        private var recreateSource: RecreateSource? = null

        internal fun launch(activity: Activity, action: Runnable) {
            if (recreateSource != null) {
                return
            }

            val window = activity.window
            val decorView = window.decorView
            decorView.isDrawingCacheEnabled = true
            val b = decorView.drawingCache
            val bitmap = Bitmap.createBitmap(b)
            decorView.isDrawingCacheEnabled = false

            recreateSource = RecreateSource(
                bitmap = bitmap,
                action = action,
            )

            val intent = Intent(activity, RecreateAnimActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        }
    }

    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarCompat.transparent(this)

        if (recreateSource == null) {
            finish()
            return
        }

        val recreateSource = recreateSource!!

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageBitmap(recreateSource.bitmap)

        this.imageView = imageView

        setContentView(imageView)

        imageView.doOnPreDraw {
            recreateSource.action.run()
            imageView.post {
                imageView.post {
                    doAnim(imageView)
                }
            }
        }
    }

    private fun doAnim(imageView: ImageView) {
        imageView.animate()
            .setDuration(500)
            .alpha(0F)
            .withEndAction {
                finish()
                recreateSource?.recycle()
                recreateSource = null
            }
            .start()
    }

    override fun swipeBackDirection(): Int {
        return 0
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}