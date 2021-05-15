package per.goweii.wanandroid.module.login.view

import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.module.login.model.UserEntity

interface QuickLoginView : BaseView {
    fun loginSuccess(code: Int, data: UserEntity)
    fun loginFailed(code: Int, msg: String?)
}