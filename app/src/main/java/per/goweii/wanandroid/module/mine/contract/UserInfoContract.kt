package per.goweii.wanandroid.module.mine.contract

import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.login.model.UserEntity
import per.goweii.wanandroid.utils.UserUtils

/**
 * @author CuiZhen
 * @date 2020/5/27
 */
interface UserInfoView : BaseView {
    fun gotoLogin()
    fun mineInfoSuccess(userEntity: UserEntity)
}

class UserInfoPresenter : BasePresenter<UserInfoView>() {
    fun mineInfo() {
        if (!UserUtils.getInstance().isLogin) {
            if (isAttach) {
                baseView.gotoLogin()
            }
            return
        }
        UserUtils.getInstance().loginUser.let {
            if (isAttach) {
                baseView.mineInfoSuccess(it)
            }
        }
    }
}
