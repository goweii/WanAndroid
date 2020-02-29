package per.goweii.wanandroid.module.main.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.zxing.BarcodeFormat
import com.king.zxing.CaptureHelper
import com.king.zxing.OnCaptureCallback
import kotlinx.android.synthetic.main.activity_scan.*
import per.goweii.anypermission.RequestListener
import per.goweii.anypermission.RuntimeRequester
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.utils.LogUtils
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.ext.visible
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ScanPresenter
import per.goweii.wanandroid.module.main.view.ScanView
import per.goweii.wanandroid.utils.PictureSelectorUtils
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/2/26
 */
class ScanActivity : BaseActivity<ScanPresenter>(), ScanView, OnCaptureCallback {

    companion object {
        private const val REQ_CODE_CAMERA = 1
        private const val REQ_CODE_SELECT_PIC = 2

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ScanActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var mCaptureHelper: CaptureHelper
    private var mRuntimeRequester: RuntimeRequester? = null
    private var hasPermission = false
    private var tvTipAnim: Animator? = null

    override fun swipeBackOnlyEdge(): Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_scan

    override fun initPresenter(): ScanPresenter? = ScanPresenter()

    override fun initView() {
        requestPermission()
        ivAlbum.setOnClickListener {
            PictureSelectorUtils.ofImage(this@ScanActivity, REQ_CODE_SELECT_PIC)
        }
        tvTip.visibility = View.INVISIBLE
        ivTorch.visibility = View.INVISIBLE
        mCaptureHelper = CaptureHelper(this, surfaceView, viewfinderView, ivTorch)
                .setOnCaptureCallback(this)
                .continuousScan(false)
                .vibrate(true)
                .playBeep(true)
                .fullScreenScan(true)
                .supportAutoZoom(false)
                .supportZoom(true)
                .decodeFormats(arrayListOf(BarcodeFormat.QR_CODE))
        mCaptureHelper.onCreate()
    }

    private fun requestPermission() {
        mRuntimeRequester = PermissionUtils.request(object : RequestListener {
            override fun onSuccess() {
                hasPermission = true
                hideTvTip()
            }

            override fun onFailed() {
                hasPermission = false
                showTvTip("为获取到相机权限\n点击重试", View.OnClickListener {
                    hideTvTip()
                    requestPermission()
                })
            }
        }, this, REQ_CODE_CAMERA, Manifest.permission.CAMERA)
    }

    override fun loadData() {
    }

    override fun onResume() {
        super.onResume()
        if (!shouldPause) {
            mCaptureHelper.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        mCaptureHelper.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCaptureHelper.onDestroy()
        cancelTvTipAnim()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mCaptureHelper.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private var shouldPause = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_CAMERA -> {
                mRuntimeRequester?.onActivityResult(requestCode)
            }
            REQ_CODE_SELECT_PIC -> {
                PictureSelectorUtils.forResult(resultCode, data)?.let { path ->
                    val result: String? = presenter.parseCode(path)
                    LogUtils.d("ScanActivity", "result=$result")
                    if (result == null) {
                        shouldPause = true
                        mCaptureHelper.onPause()
                        showTvTip("未识别到二维码", View.OnClickListener {
                            hideTvTip()
                            shouldPause = false
                            mCaptureHelper.onResume()
                            mCaptureHelper.surfaceCreated(surfaceView.holder)
                        })
                    } else {
                        UrlOpenUtils.with(result).open(context)
                        finish()
                    }
                }
            }
        }
    }

    override fun onResultCallback(result: String?): Boolean {
        LogUtils.d("ScanActivity", "result=$result")
        UrlOpenUtils.with(result).open(context)
        finish()
        return true
    }

    private fun cancelTvTipAnim() {
        tvTipAnim?.cancel()
        tvTipAnim = null
    }

    private fun showTvTip(text: String, listener: View.OnClickListener) {
        tvTip ?: return
        tvTip.text = text
        tvTip.setOnClickListener(listener)
        cancelTvTipAnim()
        tvTipAnim = ObjectAnimator.ofFloat(tvTip, "alpha", 0F, 1F).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    tvTip ?: return
                    tvTip.visible()
                }
            })
            start()
        }
    }

    private fun hideTvTip() {
        tvTip ?: return
        cancelTvTipAnim()
        if (tvTip.visibility != View.VISIBLE) {
            return
        }
        tvTip.text = ""
        tvTip.setOnClickListener(null)
        tvTipAnim = ObjectAnimator.ofFloat(tvTip, "alpha", 1F, 0F).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    tvTip ?: return
                    tvTip.gone()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    tvTip ?: return
                    tvTip.visible()
                }
            })
            start()
        }
    }
}