package per.goweii.wanandroid.module.main.dialog

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import per.goweii.anylayer.Layer
import per.goweii.anylayer.dialog.DialogLayer
import per.goweii.anylayer.utils.AnimatorHelper
import per.goweii.anylayer.utils.Utils
import per.goweii.anypermission.RequestListener
import per.goweii.anypermission.RuntimeRequester
import per.goweii.basic.core.glide.GlideHelper
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.ResUtils
import per.goweii.basic.utils.ShareUtils
import per.goweii.basic.utils.bitmap.BitmapUtils
import per.goweii.codex.encoder.CodeEncoder
import per.goweii.codex.processor.zxing.ZXingEncodeQRCodeProcessor
import per.goweii.wanandroid.R
import per.goweii.wanandroid.utils.ImageLoader
import java.util.*

class ArticleShareDialog(
        context: Context,
        covers: List<String?>?,
        private var title: String,
        private var desc: String,
        private var url: String
) : DialogLayer(context), Layer.AnimatorCreator {
    private val reqCodePermission = 99
    private var runtimeRequester: RuntimeRequester? = null
    private var permissionFragment: PermissionFragment? = null

    private val allCovers = arrayListOf<String?>().apply {
        add(null)
        covers?.filter { !it.isNullOrBlank() }
                ?.forEach { add(it) }
    }

    private val hasCover: Boolean
        get() = null != allCovers.find { !it.isNullOrBlank() }

    private var curCover: String? = null

    override fun onCreate() {
        setContentView(R.layout.dialog_article_share)
        setBackgroundDimDefault()
        setContentAnimator(this)
        addOnClickToDismissListener(R.id.dialog_article_share_ll_card)
        addOnClickToDismissListener({ _, _ ->
            createCardBitmap()?.let { saveBitmap(it) }
        }, R.id.dialog_article_share_ll_album)
        addOnClickToDismissListener({ _, _ ->
            createCardBitmap()?.let { shareBitmap(it) }
        }, R.id.dialog_article_share_ll_share)
        super.onCreate()
    }

    override fun createInAnimator(target: View): Animator {
        val srl_content = requireView<View>(R.id.dialog_article_share_srl_content)
        val ll_bottom = requireView<View>(R.id.dialog_article_share_ll_bottom)
        val animator = AnimatorSet()
        animator.playTogether(
                AnimatorHelper.createZoomAlphaInAnim(srl_content),
                AnimatorHelper.createBottomInAnim(ll_bottom)
        )
        return animator
    }

    override fun createOutAnimator(target: View): Animator {
        val srl_content = requireView<View>(R.id.dialog_article_share_srl_content)
        val ll_bottom = requireView<View>(R.id.dialog_article_share_ll_bottom)
        val animator = AnimatorSet()
        animator.playTogether(
                AnimatorHelper.createZoomAlphaOutAnim(srl_content),
                AnimatorHelper.createBottomOutAnim(ll_bottom)
        )
        return animator
    }

    override fun onAttach() {
        super.onAttach()
        val rv_covers = requireView<RecyclerView>(R.id.dialog_article_share_rv_covers)
        val iv_qrcode = requireView<ImageView>(R.id.dialog_article_share_iv_qrcode)
        val tv_title = requireView<TextView>(R.id.dialog_article_share_tv_title)
        val tv_desc = requireView<TextView>(R.id.dialog_article_share_tv_desc)

        if (hasCover) {
            rv_covers.isVisible = true
            rv_covers.layoutManager = LinearLayoutManager(rv_covers.context, LinearLayoutManager.HORIZONTAL, false)
            rv_covers.adapter = CoverAdapter()
            LinearSnapHelper().attachToRecyclerView(rv_covers)
        } else {
            rv_covers.isVisible = false
        }

        tv_title.text = title
        tv_desc.text = desc
        CodeEncoder(ZXingEncodeQRCodeProcessor()).encode(url, {
            iv_qrcode.setImageBitmap(it)
        }, {})

        bindCurCover()
    }

    override fun onPreShow() {
        super.onPreShow()
        val ll_card = requireView<LinearLayout>(R.id.dialog_article_share_ll_card)
        val ll_bottom = requireView<View>(R.id.dialog_article_share_ll_bottom)
        Utils.setViewPaddingBottom(ll_card, ll_bottom.height)
    }

    override fun onDetach() {
        super.onDetach()
        permissionFragment?.removeFromActivity(activity)
    }

    private fun bindCurCover() {
        val iv_cover = requireView<ImageView>(R.id.dialog_article_share_iv_cover)
        if (curCover.isNullOrBlank()) {
            iv_cover.isVisible = false
        } else {
            GlideHelper.with(iv_cover.context).asBitmap().load(curCover).getBitmap {
                iv_cover.isVisible = true
                iv_cover.setImageBitmap(it)
            }
        }
    }

    private fun createCardBitmap(): Bitmap? {
        val view = requireView<View>(R.id.dialog_article_share_fl_card)
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

    private inner class CoverAdapter : RecyclerView.Adapter<CoverAdapter.CoverHolder>() {
        override fun getItemCount(): Int = allCovers.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_item_article_share_cover, parent, false)
            return CoverHolder(view)
        }

        override fun onBindViewHolder(holder: CoverHolder, position: Int) {
            val url = allCovers[position]
            if (!TextUtils.isEmpty(url)) {
                holder.iv_none.isVisible = false
                ImageLoader.image(holder.iv_cover, url)
            } else {
                holder.iv_none.isVisible = true
                holder.iv_cover.setImageBitmap(null)
            }
            if (TextUtils.equals(url, curCover)) {
                holder.rl_cover.setBackgroundResource(R.drawable.bg_solid_round_color_main_radius_def)
            } else {
                holder.rl_cover.setBackgroundResource(R.color.color_transparent)
            }
        }

        private inner class CoverHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val rl_cover = itemView.findViewById<RelativeLayout>(R.id.rl_cover)
            val iv_cover = itemView.findViewById<ImageView>(R.id.iv_cover)
            val iv_none = itemView.findViewById<ImageView>(R.id.iv_none)

            init {
                iv_none.setColorFilter(ResUtils.getThemeColor(activity, R.attr.colorOnSurface))
                rl_cover.setOnClickListener {
                    curCover = allCovers[adapterPosition]
                    notifyDataSetChanged()
                    bindCurCover()
                }
            }
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