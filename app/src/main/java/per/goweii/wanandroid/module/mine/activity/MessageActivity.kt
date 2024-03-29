package per.goweii.wanandroid.module.mine.activity

import android.content.Context
import android.content.Intent
import android.view.View
import butterknife.BindView
import kotlinx.android.synthetic.main.activity_message.*
import per.goweii.actionbarex.ActionBarEx
import per.goweii.actionbarex.common.ActionIconView
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.mine.fragment.MessageReadedFragment
import per.goweii.wanandroid.module.mine.fragment.MessageUnreadFragment
import per.goweii.wanandroid.utils.MagicIndicatorUtils

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
class MessageActivity : BaseActivity<BasePresenter<BaseView>>() {

    @BindView(R.id.ab)
    lateinit var ab: ActionBarEx

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, MessageActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId() = R.layout.activity_message

    override fun initPresenter(): BasePresenter<BaseView>? = null

    override fun initView() {
        ab.getView<ActionIconView>(R.id.action_bar_fixed_magic_indicator_iv_back).apply {
            visibility = View.VISIBLE
            setOnClickListener {
                finish()
            }
        }
        val adapter = FixedFragmentPagerAdapter(supportFragmentManager)
        adapter.setTitles("新消息", "历史消息")
        adapter.setFragmentList(
                MessageUnreadFragment.create(),
                MessageReadedFragment.create()
        )
        vp.adapter = adapter
        MagicIndicatorUtils.commonNavigator(ab.getView(R.id.mi), vp, adapter, null)
    }

    override fun loadData() {}
}