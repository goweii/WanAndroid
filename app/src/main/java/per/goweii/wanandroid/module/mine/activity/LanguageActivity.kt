package per.goweii.wanandroid.module.mine.activity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.l10n.L10nManager
import per.goweii.wanandroid.databinding.ActivityLanguageBinding
import per.goweii.wanandroid.module.mine.adapter.LanguageAdapter
import java.util.Locale

class LanguageActivity : BaseActivity<BasePresenter<*>, ActivityLanguageBinding>() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, LanguageActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var adapter: LanguageAdapter? = null

    override fun initViewBinding(inflater: LayoutInflater): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(inflater)
    }

    override fun initPresenter(): BasePresenter<*>? {
        return null
    }

    override fun initView() {
        adapter = LanguageAdapter()
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = adapter

        adapter?.setOnItemClickListener { _, _, position ->
            val locale = adapter?.getItem(position)
            L10nManager.setApplicationLocales(locale)
        }
    }

    override fun loadData() {
        val locales = arrayListOf<Locale?>()
        locales.add(null)
        locales.addAll(L10nManager.getSupportedLocales(this))
        adapter?.setNewData(locales)
    }
}