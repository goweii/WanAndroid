package per.goweii.wanandroid.module.mine.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.activity_user_info.*
import per.goweii.anypermission.RequestListener
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.permission.PermissionUtils
import per.goweii.basic.ui.dialog.ListDialog
import per.goweii.basic.ui.toast.ToastMaker
import per.goweii.basic.utils.ext.gone
import per.goweii.basic.utils.file.CacheUtils
import per.goweii.basic.utils.listener.SimpleListener
import per.goweii.wanandroid.R
import per.goweii.wanandroid.event.UserInfoUpdateEvent
import per.goweii.wanandroid.http.CmsApi
import per.goweii.wanandroid.module.login.activity.AuthActivity
import per.goweii.wanandroid.module.login.model.UserEntity
import per.goweii.wanandroid.module.mine.contract.UserInfoPresenter
import per.goweii.wanandroid.module.mine.contract.UserInfoView
import per.goweii.wanandroid.module.mine.dialog.InfoEditDialog
import per.goweii.wanandroid.utils.ImageLoader
import per.goweii.wanandroid.utils.PictureSelector
import per.goweii.wanandroid.utils.UserUtils
import java.io.File
import java.io.IOException

/**
 * @author CuiZhen
 * @date 2020/5/27
 */
class UserInfoActivity : BaseActivity<UserInfoPresenter>(), UserInfoView {

    companion object {
        const val REQUEST_CODE_PERMISSION_ALBUM = 1
        const val REQUEST_CODE_SELECT_USER_ICON = 2
        const val REQUEST_CODE_CROP_USER_ICON = 3
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, UserInfoActivity::class.java))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_user_info

    override fun initPresenter(): UserInfoPresenter = UserInfoPresenter()

    override fun initView() {
        if (!CmsApi.isEnabled) {
            iv_user_icon_edit.gone()
            tv_user_icon_edit_tip.gone()
            ll_user_email.gone()
            ll_user_sex.gone()
            ll_user_signature.gone()
            ll_user_cmsid.gone()
            v_line.gone()
        }
        iv_blur.setOnLongClickListener {
            if (CmsApi.isEnabled) {
                showCoverUrlInputDialog()
            }
            return@setOnLongClickListener true
        }
        iv_user_icon_edit.setOnClickListener {
            if (CmsApi.isEnabled) {
                showAvatarUrlInputDialog()
            }
        }
        ll_user_email.setOnClickListener {
            if (CmsApi.isEnabled) {
                showEmailInputDialog()
            }
        }
        ll_user_sex.setOnClickListener {
            if (CmsApi.isEnabled) {
                showSexChoiceDialog()
            }
        }
        ll_user_signature.setOnClickListener {
            if (CmsApi.isEnabled) {
                showSignatureChoiceDialog()
            }
        }
    }

    override fun loadData() {
        presenter.mineInfo()
    }

    private fun showCoverUrlInputDialog() {
        InfoEditDialog.with(this)
                .title("封面")
                .hint("请输入有效的图片地址")
                .text(UserUtils.getInstance().loginUser.cover)
                .show {
                    if (it != UserUtils.getInstance().loginUser.cover) {
                        presenter.updateCover(it)
                    }
                }
    }

    private fun showAvatarUrlInputDialog() {
        InfoEditDialog.with(this)
                .title("头像")
                .hint("请输入有效的图片地址")
                .text(UserUtils.getInstance().loginUser.avatar)
                .show {
                    if (it != UserUtils.getInstance().loginUser.avatar) {
                        presenter.updateAvatar(it)
                    }
                }
    }

    private fun showEmailInputDialog() {
        InfoEditDialog.with(this)
                .title("邮箱")
                .hint("请输入邮箱")
                .text(UserUtils.getInstance().loginUser.email)
                .show {
                    if (it != UserUtils.getInstance().loginUser.email) {
                        presenter.updateEmail(it)
                    }
                }
    }

    private fun showSexChoiceDialog() {
        ListDialog.with(context)
                .noBtn()
                .cancelable(true)
                .datas("保密", "男", "女")
                .currSelectPos(UserUtils.getInstance().loginUser.sex)
                .listener { _, pos ->
                    presenter.updateSex(pos)
                }
                .show()
    }

    private fun showSignatureChoiceDialog() {
        InfoEditDialog.with(this)
                .title("签名")
                .hint("写个签名鼓励下自己吧")
                .text(UserUtils.getInstance().loginUser.signature)
                .show {
                    if (it != UserUtils.getInstance().loginUser.signature) {
                        presenter.updateSignature(it)
                    }
                }
    }

    private fun requestAlbumPermission(listener: SimpleListener) {
        PermissionUtils.request(object : RequestListener {
            override fun onSuccess() {
                listener.onResult()
            }

            override fun onFailed() {
                ToastMaker.showShort("获取存储设备读取权限失败")
            }
        }, context, REQUEST_CODE_PERMISSION_ALBUM, Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SELECT_USER_ICON -> {
                val userIconUri = PictureSelector.result(resultCode, data)
                if (userIconUri != null) {
                    val sourceFile = PictureSelector.getFileFormUri(this, userIconUri)
                    if (sourceFile != null) {
                        val file = File(CacheUtils.getCacheDir(), "user_info")
                        if (!file.exists()) {
                            file.mkdirs()
                        }
                        val clipFile = File(file, "user_icon.jpeg")
                        try {
                            clipFile.createNewFile()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        PictureSelector.crop(this, sourceFile, clipFile, REQUEST_CODE_CROP_USER_ICON)
                    }
                }
            }
            REQUEST_CODE_CROP_USER_ICON -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(CacheUtils.getCacheDir(), "user_info")
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                    val clipFile = File(file, "user_icon.jpeg")
                    if (clipFile.exists()) {
                        val path = clipFile.absolutePath
                    }
                }
            }
        }
    }

    override fun gotoLogin() {
        AuthActivity.startQuickLogin(context)
        finish()
    }

    override fun mineInfoSuccess(userEntity: UserEntity) {
        UserInfoUpdateEvent().post()
        ImageLoader.userIcon(civ_user_icon, userEntity.avatar ?: "")
        ImageLoader.userBlur(iv_blur, userEntity.cover ?: "")
        tv_user_name.text = userEntity.username
        tv_user_id.text = userEntity.wanid.toString()
        tv_user_cmsid.text = userEntity.cmsid
        tv_user_email.text = userEntity.email
        tv_user_sex.text = when (userEntity.sex) {
            1 -> "男"
            2 -> "女"
            else -> "保密"
        }
        if (userEntity.signature.isNullOrEmpty()) {
            tv_user_signature.text = "写个签名鼓励下自己吧"
        } else {
            tv_user_signature.text = userEntity.signature
        }
    }

    override fun mineInfoFailed(msg: String) {
        ToastMaker.showShort(msg)
    }
}