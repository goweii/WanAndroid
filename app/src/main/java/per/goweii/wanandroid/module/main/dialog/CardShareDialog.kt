package per.goweii.wanandroid.module.main.dialog

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import per.goweii.anylayer.Layer
import per.goweii.anylayer.dialog.DialogLayer
import per.goweii.anylayer.utils.AnimatorHelper
import per.goweii.anypermission.RequestListener
import per.goweii.anypermission.RuntimeRequester
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.ShareUtils
import per.goweii.basic.utils.bitmap.BitmapUtils
import per.goweii.wanandroid.R

class CardShareDialog(
        context: Context,
        private var layoutId: Int,
        private var bindData: (View) -> Unit
) : DialogLayer(context), Layer.AnimatorCreator {
    private val reqCodePermission = 99
    private var runtimeRequester: RuntimeRequester? = null
    private var permissionFragment: PermissionFragment? = null

    init {
        setContentView(R.layout.dialog_card_share)
        setBackgroundDimDefault()
        setContentAnimator(this)
        addOnClickToDismissListener(R.id.dialog_card_share_rl_content)
        addOnClickToDismissListener(R.id.dialog_card_share_fl_card)
        addOnClickToDismissListener(R.id.dialog_card_share_srl_card)
        addOnClickToDismissListener(R.id.dialog_card_share_nsv_card)
        addOnClickToDismissListener(R.id.dialog_card_share_ll_card)
        addOnClickToDismissListener(R.id.dialog_card_share_ll_btn)
        addOnClickToDismissListener(R.id.dialog_card_share_ll_album)
        addOnClickToDismissListener(R.id.dialog_card_share_ll_share)
        addOnClickToDismissListener({ _, _ ->
            createCardBitmap()?.let { saveBitmap(it) }
        }, R.id.dialog_card_share_iv_album)
        addOnClickToDismissListener({ _, _ ->
            createCardBitmap()?.let { shareBitmap(it) }
        }, R.id.dialog_card_share_iv_share)
    }

    override fun createInAnimator(target: View): Animator {
        val nsv_card = requireView<View>(R.id.dialog_card_share_srl_card)
        val ll_btn = requireView<View>(R.id.dialog_card_share_ll_btn)
        val ll_album = requireView<View>(R.id.dialog_card_share_ll_album)
        val ll_share = requireView<View>(R.id.dialog_card_share_ll_share)
        val animator = AnimatorSet()
        animator.playTogether(
                AnimatorHelper.createAlphaInAnim(nsv_card),
                AnimatorHelper.createAlphaInAnim(ll_btn),
                AnimatorHelper.createZoomAlphaInAnim(ll_album, 0.6F),
                AnimatorHelper.createZoomAlphaInAnim(ll_share, 0.6F)
        )
        return animator
    }

    override fun createOutAnimator(target: View): Animator {
        val nsv_card = requireView<View>(R.id.dialog_card_share_srl_card)
        val ll_btn = requireView<View>(R.id.dialog_card_share_ll_btn)
        val ll_album = requireView<View>(R.id.dialog_card_share_ll_album)
        val ll_share = requireView<View>(R.id.dialog_card_share_ll_share)
        val animator = AnimatorSet()
        animator.playTogether(
                AnimatorHelper.createAlphaOutAnim(nsv_card),
                AnimatorHelper.createAlphaOutAnim(ll_btn),
                AnimatorHelper.createZoomAlphaOutAnim(ll_album, 0.6F),
                AnimatorHelper.createZoomAlphaOutAnim(ll_share, 0.6F)
        )
        return animator
    }

    override fun onAttach() {
        super.onAttach()
        val fl_card = requireView<ViewGroup>(R.id.dialog_card_share_ll_card)
        val view = LayoutInflater.from(activity).inflate(layoutId, fl_card, false)
        view.isClickable = true
        fl_card.removeAllViews()
        fl_card.addView(view)
        bindData.invoke(view)
    }

    override fun onDetach() {
        super.onDetach()
        val fl_card = requireView<ViewGroup>(R.id.dialog_card_share_ll_card)
        fl_card.removeAllViews()
        permissionFragment?.removeFromActivity(activity)
    }

    private fun createCardBitmap(): Bitmap? {
        val fl_card = requireView<ViewGroup>(R.id.dialog_card_share_ll_card)
        if (fl_card.childCount == 0) return null
        val view = fl_card.getChildAt(0)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun shareBitmap(bitmap: Bitmap) {
        ShareUtils.shareBitmap(activity, bitmap)
    }

    private fun saveBitmap(bitmap: Bitmap) {
        runtimeRequester = PermissionUtils.request(object : RequestListener {
            override fun onSuccess() {
                if (BitmapUtils.saveGallery(bitmap, "wanandroid_app_" + System.currentTimeMillis())) {
                    ToastMaker.showShort("保存成功")
                } else {
                    ToastMaker.showShort("保存失败")
                }
                bitmap.recycle()
            }

            override fun onFailed() {
                ToastMaker.showShort("授权失败")
                bitmap.recycle()
            }
        }, activity, reqCodePermission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionFragment = PermissionFragment.findOrAddToActivity(activity)
        permissionFragment?.onActivityResult = {
            runtimeRequester?.onActivityResult(it)
        }
    }

    private class PermissionFragment : Fragment() {
        var onActivityResult: ((requestCode: Int) -> Unit)? = null

        companion object {
            fun findOrAddToActivity(activity: Activity): PermissionFragment {
                activity as AppCompatActivity
                activity.supportFragmentManager.findFragmentByTag(PermissionFragment::class.java.name)
                        ?.let { return it as PermissionFragment }
                return PermissionFragment().also { fg ->
                    activity.supportFragmentManager.beginTransaction().apply {
                        add(fg, PermissionFragment::class.java.name)
                    }.commitNowAllowingStateLoss()
                }
            }
        }

        fun removeFromActivity(activity: Activity) {
            activity as AppCompatActivity
            activity.supportFragmentManager.beginTransaction().apply {
                remove(this@PermissionFragment)
            }.commitNowAllowingStateLoss()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            onActivityResult?.invoke(requestCode)
        }
    }
}