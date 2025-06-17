package per.goweii.wanandroid.module.mine.activity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import per.goweii.basic.core.base.BaseActivity
import per.goweii.wanandroid.R
import per.goweii.wanandroid.databinding.ActivityUserInfoBinding
import per.goweii.wanandroid.event.UserInfoUpdateEvent
import per.goweii.wanandroid.module.login.activity.AuthActivity
import per.goweii.wanandroid.module.login.model.UserEntity
import per.goweii.wanandroid.module.mine.contract.UserInfoPresenter
import per.goweii.wanandroid.module.mine.contract.UserInfoView
import per.goweii.wanandroid.utils.ImageLoader

/**
 * @author CuiZhen
 * @date 2020/5/27
 */
class UserInfoActivity : BaseActivity<UserInfoPresenter, ActivityUserInfoBinding>(), UserInfoView {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, UserInfoActivity::class.java))
        }
    }

    override fun initViewBinding(inflater: LayoutInflater): ActivityUserInfoBinding {
        return ActivityUserInfoBinding.inflate(inflater)
    }

    override fun initPresenter(): UserInfoPresenter = UserInfoPresenter()

    override fun initView() {
    }

    override fun loadData() {
        presenter.mineInfo()
    }

    override fun gotoLogin() {
        AuthActivity.startQuickLogin(context)
        finish()
    }

    override fun mineInfoSuccess(userEntity: UserEntity) {
        UserInfoUpdateEvent().post()
        ImageLoader.userIcon(binding.civUserIcon, userEntity.avatar ?: "")
        ImageLoader.userBlur(binding.ivBlur, userEntity.cover ?: "")
        binding.tvUserName.text = userEntity.username
        binding.tvUserId.text = userEntity.wanid.toString()
    }
}