package per.goweii.wanandroid.module.mine.activity

import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.activity_user_info.*
import per.goweii.basic.core.base.BaseActivity
import per.goweii.wanandroid.R
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
class UserInfoActivity : BaseActivity<UserInfoPresenter>(), UserInfoView {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, UserInfoActivity::class.java))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_user_info

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
        ImageLoader.userIcon(civ_user_icon, userEntity.avatar ?: "")
        ImageLoader.userBlur(iv_blur, userEntity.cover ?: "")
        tv_user_name.text = userEntity.username
        tv_user_id.text = userEntity.wanid.toString()
    }
}