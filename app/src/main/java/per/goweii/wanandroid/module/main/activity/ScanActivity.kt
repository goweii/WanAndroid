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
import per.goweii.anypermission.AnyPermission
import per.goweii.anypermission.RequestListener
import per.goweii.anypermission.RuntimeRequester
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.utils.LogUtils
import per.goweii.basic.utils.bitmap.BitmapUtils
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.ext.invisible
import per.goweii.basic.utils.ext.visible
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ScanPresenter
import per.goweii.wanandroid.module.main.view.ScanView
import per.goweii.wanandroid.utils.PictureSelector
import per.goweii.wanandroid.utils.UrlOpenUtils


/**
 * @author CuiZhen
 * @date 2020/2/26
 */
class ScanActivity : BaseActivity<ScanPresenter>(), ScanView, QRCodeView.Delegate {

    companion object {
        private const val REQ_CODE_PERMISSION_CAMERA = 1
        private const val REQ_CODE_PERMISSION_ALBUM = 2
        private const val REQ_CODE_SELECT_PIC = 3

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ScanActivity::class.java)
            context.startActivity(intent)
        }
    }

    enum class Mode {
        SCAN, ALBUM
    }

    private var mRuntimeRequester: RuntimeRequester? = null
    private var tvTipAnim: Animator? = null
    private var mode = Mode.SCAN

    override fun swipeBackOnlyEdge(): Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_scan

    override fun initPresenter(): ScanPresenter? = ScanPresenter()

    override fun initView() {
        llTip.visibility = View.INVISIBLE
        ivTorch.visibility = View.INVISIBLE
        qrCodeView.setDelegate(this)
        qrCodeView.stopSpotAndHiddenRect()
        ivAlbum.setOnClickListener {
            startModeAlbum()
        }
    }

    override fun loadData() {
    }

    override fun onResume() {
        super.onResume()
        if (mode == Mode.SCAN) {
            startScan()
        }
    }

    override fun onPause() {
        super.onPause()
        stopScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTipAnim()
        qrCodeView.onDestroy()
    }

    private fun startModeScan() {
        mode = Mode.SCAN
        startScan()
    }

    private fun startScan() {
        mRuntimeRequester = AnyPermission.with(context)
                .runtime(REQ_CODE_PERMISSION_CAMERA)
                .permissions(Manifest.permission.CAMERA)
                .onBeforeRequest { _, executor ->
                    showTip("扫码二维码/条码需要相机权限", "点击申请", View.OnClickListener {
                        hideTip()
                        executor.execute()
                    })
                }
                .onBeenDenied { _, executor ->
                    showTip("拒绝相机权限将无法扫码", "重新申请", View.OnClickListener {
                        hideTip()
                        executor.execute()
                    })
                }
                .onGoSetting { _, executor ->
                    showTip("相机权限已被拒绝", "去设置", View.OnClickListener {
                        hideTip()
                        executor.execute()
                    })
                }
                .request(object : RequestListener {
                    override fun onSuccess() {
                        qrCodeView.startCamera()
                        qrCodeView.startSpotAndShowRect()
                    }

                    override fun onFailed() {
                        showTip("无法获取相机权限", "点击获取", View.OnClickListener {
                            hideTip()
                            startModeScan()
                        })
                    }
                })
    }

    private fun stopScan() {
        qrCodeView.stopSpotAndHiddenRect()
        qrCodeView.stopCamera()
    }

    private fun startModeAlbum() {
        mode = Mode.ALBUM
        stopScan()
        startAlbum()
    }

    private fun startAlbum() {
        mRuntimeRequester = AnyPermission.with(context)
                .runtime(REQ_CODE_PERMISSION_ALBUM)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onBeforeRequest { _, executor ->
                    showTip("选择图片需要存储权限", "点击申请", View.OnClickListener {
                        hideTip()
                        executor.execute()
                    }, "返回扫码", View.OnClickListener {
                        hideTip()
                        executor.cancel()
                    })
                }
                .onBeenDenied { _, executor ->
                    showTip("拒绝存储权限将无法选择图片", "重新申请", View.OnClickListener {
                        hideTip()
                        executor.execute()
                    }, "返回扫码", View.OnClickListener {
                        hideTip()
                        executor.cancel()
                    })
                }
                .onGoSetting { _, executor ->
                    showTip("存储权限已被拒绝", "去设置", View.OnClickListener {
                        hideTip()
                        executor.execute()
                    }, "返回扫码", View.OnClickListener {
                        hideTip()
                        executor.cancel()
                    })
                }
                .request(object : RequestListener {
                    override fun onSuccess() {
                        PictureSelector.select(this@ScanActivity, REQ_CODE_SELECT_PIC)
                    }

                    override fun onFailed() {
                        startModeScan()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_PERMISSION_CAMERA -> {
                mRuntimeRequester?.onActivityResult(requestCode)
            }
            REQ_CODE_PERMISSION_ALBUM -> {
                mRuntimeRequester?.onActivityResult(requestCode)
            }
            REQ_CODE_SELECT_PIC -> {
                PictureSelector.result(resultCode, data)?.let {
                    BitmapUtils.getBitmapFromUri(context, it)?.let { bitmap ->
                        QRCodeDecoder.syncDecodeQRCode(bitmap)?.let { result ->
                            onAlbumQRCodeSuccess(result)
                        } ?: run {
                            showTip("没有识别到二维码/条码等", "返回扫码", View.OnClickListener {
                                hideTip()
                                startModeScan()
                            })
                        }
                    } ?: run {
                        showTip("打开图片失败", "返回扫码", View.OnClickListener {
                            hideTip()
                            startModeScan()
                        })
                    }
                } ?: run {
                    startModeScan()
                }
            }
        }
    }

    override fun onScanQRCodeSuccess(result: String?) {
        LogUtils.d("ScanActivity", "result=$result")
        stopScan()
        vibrate()
        UrlOpenUtils.with(result).open(context)
        finish()
    }

    private fun onAlbumQRCodeSuccess(result: String?) {
        LogUtils.d("ScanActivity", "result=$result")
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
        showTip("打开相机失败", "点击重试", View.OnClickListener {
            hideTip()
            startModeScan()
        })
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    private fun cancelTipAnim() {
        tvTipAnim?.cancel()
        tvTipAnim = null
    }

    private fun showTip(text: String, btnSure: String, onSure: View.OnClickListener, btnCancel: String? = null, onCancel: View.OnClickListener? = null) {
        llTip ?: return
        tvTipText.text = text
        tvTipBtnSure.text = btnSure
        if (btnCancel.isNullOrEmpty()) {
            tvTipBtnCancel.gone()
        } else {
            tvTipBtnCancel.visible()
            tvTipBtnCancel.text = btnCancel
            tvTipBtnCancel.setOnClickListener(onCancel)
        }
        llTip.setOnClickListener(onSure)
        cancelTipAnim()
        tvTipAnim = ObjectAnimator.ofFloat(llTip, "alpha", 0F, 1F).apply {
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
                    llTip ?: return
                    llTip.visible()
                }
            })
            start()
        }
    }

    private fun hideTip() {
        llTip ?: return
        cancelTipAnim()
        if (llTip.visibility != View.VISIBLE) {
            return
        }
        tvTipText.text = ""
        tvTipBtnSure.text = ""
        tvTipBtnCancel.text = ""
        tvTipBtnCancel.gone()
        tvTipBtnCancel.setOnClickListener(null)
        llTip.setOnClickListener(null)
        tvTipAnim = ObjectAnimator.ofFloat(llTip, "alpha", 1F, 0F).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    llTip ?: return
                    llTip.gone()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    llTip ?: return
                    llTip.visible()
                }
            })
            start()
        }
    }
}