package per.goweii.wanandroid.module.main.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.animation.DecelerateInterpolator
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
import per.goweii.codex.decoder.CodeDecoder
import per.goweii.codex.decorator.beep.BeepDecorator
import per.goweii.codex.decorator.gesture.GestureDecorator
import per.goweii.codex.decorator.vibrate.VibrateDecorator
import per.goweii.codex.processor.zxing.ZXingMultiDecodeQRCodeProcessor
import per.goweii.codex.processor.zxing.ZXingMultiScanQRCodeProcessor
import per.goweii.codex.scanner.CameraProxy
import per.goweii.codex.scanner.CodeScanner
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.swipeback.SwipeBackAbility
import per.goweii.swipeback.SwipeBackDirection
import per.goweii.swipeback.SwipeBackTransformer
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ScanPresenter
import per.goweii.wanandroid.module.main.view.ScanView
import per.goweii.wanandroid.utils.PictureSelector
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/2/26
 */
class ScanActivity : BaseActivity<ScanPresenter>(), ScanView, SwipeBackAbility.Direction, SwipeBackAbility.Transformer {

    companion object {
        private const val REQ_CODE_PERMISSION_CAMERA = 1
        private const val REQ_CODE_PERMISSION_ALBUM = 2
        private const val REQ_CODE_SELECT_PIC = 3

        @JvmStatic
        fun start(activity: Activity) {
            val intent = Intent(activity, ScanActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(
                    R.anim.swipeback_activity_open_bottom_in,
                    R.anim.swipeback_activity_open_alpha_out
            )
        }

        @JvmStatic
        fun startForResult(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, ScanActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(
                    R.anim.swipeback_activity_open_bottom_in,
                    R.anim.swipeback_activity_open_alpha_out
            )
        }
    }

    private var mRuntimeRequester: RuntimeRequester? = null
    private var tvTipAnim: Animator? = null

    private var codeScanner: CodeScanner? = null

    override fun swipeBackDirection(): SwipeBackDirection = SwipeBackDirection.BOTTOM

    override fun swipeBackTransformer(): SwipeBackTransformer? = null

    override fun getLayoutId(): Int = R.layout.activity_scan

    override fun initPresenter(): ScanPresenter = ScanPresenter()

    override fun initView() {
        StatusBarCompat.transparent(this)
        ivClose.setOnClickListener {
            finish()
        }
        ivTorch.invisible()
        ivAlbum.setOnClickListener {
            startAlbum()
        }
        ivTorch.setOnClickListener {
            codeScanner?.enableTorch(!ivTorch.isSelected)
        }
        codeScanner = code_scanner.apply {
            cameraProxyLiveData.observe(this@ScanActivity) { cameraProxy ->
                cameraProxy?.torchState?.observe(this@ScanActivity) { torchState ->
                    when (torchState) {
                        CameraProxy.TORCH_ON -> ivTorch.isSelected = true
                        CameraProxy.TORCH_OFF -> ivTorch.isSelected = false
                    }
                }
            }
            addProcessor(ZXingMultiScanQRCodeProcessor())
            addDecorator(
                    frozen_view,
                    finder_view,
                    BeepDecorator(),
                    VibrateDecorator(),
                    GestureDecorator()
            )
            onFound {
                onScanQRCodeSuccess(it.first().text)
            }
            bindToLifecycle(this@ScanActivity)
        }
        startScan()
    }

    override fun loadData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTipAnim()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
                R.anim.swipeback_activity_close_alpha_in,
                R.anim.swipeback_activity_close_bottom_out
        )
    }

    private fun startScan() {
        mRuntimeRequester = AnyPermission.with(context)
                .runtime(REQ_CODE_PERMISSION_CAMERA)
                .permissions(Manifest.permission.CAMERA)
                .onBeforeRequest { _, executor ->
                    showTip("扫二维码需要相机权限", "点击申请", {
                        hideTip()
                        executor.execute()
                    })
                }
                .onBeenDenied { _, executor ->
                    showTip("拒绝相机权限将无法扫码", "重新申请", {
                        hideTip()
                        executor.execute()
                    })
                }
                .onGoSetting { _, executor ->
                    showTip("相机权限已被拒绝", "去设置", {
                        hideTip()
                        executor.execute()
                    })
                }
                .request(object : RequestListener {
                    @SuppressLint("MissingPermission")
                    override fun onSuccess() {
                        ivTorch.visible()
                        finder_view.visible()
                        codeScanner?.startScan()
                    }

                    override fun onFailed() {
                        showTip("无法获取相机权限", "点击获取", {
                            hideTip()
                            startScan()
                        })
                    }
                })
    }

    private fun stopScan() {
        ivTorch.invisible()
        finder_view.invisible()
        codeScanner?.stopScan()
    }

    private fun startAlbum() {
        stopScan()
        mRuntimeRequester = AnyPermission.with(context)
                .runtime(REQ_CODE_PERMISSION_ALBUM)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onBeforeRequest { _, executor ->
                    showTip("选择图片需要存储权限", "点击申请", {
                        hideTip()
                        executor.execute()
                    }, "返回扫码", {
                        hideTip()
                        executor.cancel()
                    })
                }
                .onBeenDenied { _, executor ->
                    showTip("拒绝存储权限将无法选择图片", "重新申请", {
                        hideTip()
                        executor.execute()
                    }, "返回扫码", {
                        hideTip()
                        executor.cancel()
                    })
                }
                .onGoSetting { _, executor ->
                    showTip("存储权限已被拒绝", "去设置", {
                        hideTip()
                        executor.execute()
                    }, "返回扫码", {
                        hideTip()
                        executor.cancel()
                    })
                }
                .request(object : RequestListener {
                    override fun onSuccess() {
                        PictureSelector.select(this@ScanActivity, REQ_CODE_SELECT_PIC)
                    }

                    override fun onFailed() {
                        startScan()
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
                        val decoder = CodeDecoder(ZXingMultiDecodeQRCodeProcessor())
                        decoder.decode(bitmap, onSuccess = { results ->
                            onAlbumQRCodeSuccess(results.first().text)
                        }, onFailure = {
                            showTip("没有识别到二维码", "返回扫码", {
                                hideTip()
                                startScan()
                            })
                        })
                    } ?: showTip("打开图片失败", "返回扫码", {
                        hideTip()
                        startScan()
                    })
                } ?: startScan()
            }
        }
    }

    private fun onScanQRCodeSuccess(result: String) {
        LogUtils.d("ScanActivity", "result=$result")
        window.decorView.postDelayed({
            UrlOpenUtils.with(result).open(context)
            finish()
        }, 300L)
    }

    private fun onAlbumQRCodeSuccess(result: String) {
        LogUtils.d("ScanActivity", "result=$result")
        window.decorView.postDelayed({
            UrlOpenUtils.with(result).open(context)
            finish()
        }, 300L)
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