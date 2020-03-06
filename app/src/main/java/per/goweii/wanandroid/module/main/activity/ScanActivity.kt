package per.goweii.wanandroid.module.main.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.view.View
import android.view.animation.DecelerateInterpolator
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import kotlinx.android.synthetic.main.activity_scan.*
import per.goweii.anypermission.RequestListener
import per.goweii.anypermission.RuntimeRequester
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.utils.LogUtils
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.ext.invisible
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
class ScanActivity : BaseActivity<ScanPresenter>(), ScanView, QRCodeView.Delegate {

    companion object {
        private const val REQ_CODE_CAMERA = 1
        private const val REQ_CODE_SELECT_PIC = 2

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ScanActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var mRuntimeRequester: RuntimeRequester? = null
    private var hasPermission = false
    private var shouldPause = false
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
        qrCodeView.setDelegate(this)
        qrCodeView.stopSpotAndHiddenRect()
    }

    private fun requestPermission() {
        mRuntimeRequester = PermissionUtils.request(object : RequestListener {
            override fun onSuccess() {
                hasPermission = true
                hideTvTip()
                if (!shouldPause) {
                    qrCodeView.startCamera()
                    qrCodeView.startSpotAndShowRect()
                }
            }

            override fun onFailed() {
                hasPermission = false
                showTvTip("没有相机权限\n点击获取", View.OnClickListener {
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
        if (hasPermission && !shouldPause) {
            qrCodeView.startCamera()
            qrCodeView.startSpotAndShowRect()
        }
    }

    override fun onPause() {
        super.onPause()
        qrCodeView.stopSpotAndHiddenRect()
        qrCodeView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        qrCodeView.onDestroy()
        cancelTvTipAnim()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_CAMERA -> {
                mRuntimeRequester?.onActivityResult(requestCode)
            }
            REQ_CODE_SELECT_PIC -> {
                shouldPause = true
                PictureSelectorUtils.forResult(resultCode, data)?.let { path ->
                    presenter.getBitmapFromPath(path)?.let { bitmap ->
                        QRCodeDecoder.syncDecodeQRCode(bitmap)?.let { result ->
                            onScanQRCodeSuccess(result)
                        } ?: run {
                            showTvTip("没有识别到二维码\n点击继续", View.OnClickListener {
                                hideTvTip()
                                shouldPause = false
                                qrCodeView.startCamera()
                                qrCodeView.startSpotAndShowRect()
                            })
                        }
                    } ?: run {
                        showTvTip("打开图片失败\n点击继续", View.OnClickListener {
                            hideTvTip()
                            shouldPause = false
                            qrCodeView.startCamera()
                            qrCodeView.startSpotAndShowRect()
                        })
                    }
                }
            }
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
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

    override fun onScanQRCodeSuccess(result: String?) {
        LogUtils.d("ScanActivity", "result=$result")
        qrCodeView.stopSpot()
        qrCodeView.stopCamera()
        vibrate()
        UrlOpenUtils.with(result).open(context)
        finish()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        if (isDark || ivTorch.isSelected) {
            ivTorch.visible()
            ivTorch.setOnClickListener {
                ivTorch.isSelected = !ivTorch.isSelected
                if (ivTorch.isSelected) {
                    qrCodeView.openFlashlight()
                } else {
                    qrCodeView.closeFlashlight()
                }
            }
        } else {
            ivTorch.invisible()
            ivTorch.isSelected = false
            ivTorch.setOnClickListener(null)
            qrCodeView.closeFlashlight()
        }
    }

    override fun onScanQRCodeOpenCameraError() {
        showTvTip("打开相机失败\n点击重试", View.OnClickListener {
            hideTvTip()
            requestPermission()
        })
    }
}