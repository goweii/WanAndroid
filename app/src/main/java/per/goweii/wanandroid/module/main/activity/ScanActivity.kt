package per.goweii.wanandroid.module.main.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.activity_scan.*
import me.devilsen.czxing.code.BarcodeFormat
import me.devilsen.czxing.view.ScanListener
import per.goweii.anypermission.RequestListener
import per.goweii.anypermission.RuntimeRequester
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.presenter.ScanPresenter
import per.goweii.wanandroid.module.main.view.ScanView
import per.goweii.wanandroid.utils.UrlOpenUtils

/**
 * @author CuiZhen
 * @date 2020/2/26
 */
class ScanActivity : BaseActivity<ScanPresenter>(), ScanView, ScanListener {

    companion object {
        private const val REQ_CODE_CAMERA = 1

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ScanActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var mRuntimeRequester: RuntimeRequester? = null
    private var hasPermission = false

    override fun swipeBackOnlyEdge(): Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_scan

    override fun initPresenter(): ScanPresenter? = null

    override fun initView() {
        scanView.setScanListener(this)
        scanView.setScanMode(me.devilsen.czxing.view.ScanView.SCAN_MODE_MIX)
        mRuntimeRequester = PermissionUtils.request(object : RequestListener {
            override fun onSuccess() {
                hasPermission = true
                scanView.openCamera()
                scanView.startScan()
                scanView.resetZoom()
            }

            override fun onFailed() {
                hasPermission = false
                finish()
            }
        }, this, REQ_CODE_CAMERA, Manifest.permission.CAMERA)
    }

    override fun loadData() {
    }

    override fun onResume() {
        super.onResume()
        scanView.openCamera()
        scanView.startScan()
        if (hasPermission) {
            scanView.resetZoom()
        }
    }

    override fun onPause() {
        super.onPause()
        scanView.stopScan()
        scanView.closeCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanView.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mRuntimeRequester?.onActivityResult(requestCode)
    }

    override fun onScanSuccess(result: String?, format: BarcodeFormat?) {
        runOnUiThread {
            UrlOpenUtils.with(result).open(context)
            finish()
        }
    }

    override fun onOpenCameraError() {
        runOnUiThread {
            ToastMaker.showShort("打开相机失败")
            finish()
        }
    }
}