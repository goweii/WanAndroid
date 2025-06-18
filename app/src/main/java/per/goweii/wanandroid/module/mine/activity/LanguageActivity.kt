package per.goweii.wanandroid.module.mine.activity

import android.content.Context
import android.content.Intent
import android.content.res.XmlResourceParser
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import per.goweii.basic.core.base.BaseActivity
import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.utils.ResUtils
import per.goweii.wanandroid.R
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
            val localeListCompat = if (locale != null) {
                LocaleListCompat.create(locale)
            } else {
                LocaleListCompat.getEmptyLocaleList()
            }
            AppCompatDelegate.setApplicationLocales(localeListCompat)
        }
    }

    override fun loadData() {
        val locales = arrayListOf<Locale?>()
        locales.add(null)
        locales.addAll(getSupportedLocales())
        adapter?.setNewData(locales)

    }

    private fun getSupportedLocales(): List<Locale> {
        val locales = arrayListOf<Locale>()
        try {
            val androidNamespace = "http://schemas.android.com/apk/res/android"
            resources.getXml(R.xml.locale_config).use { parser ->
                var next = parser.next()
                while (next != XmlResourceParser.END_DOCUMENT) {
                    if (parser.eventType == XmlResourceParser.START_TAG) {
                        if (parser.name == "locale") {
                            val name = parser.getAttributeValue(androidNamespace, "name")
                            locales.add(Locale.forLanguageTag(name))
                        }
                    }
                    next = parser.next()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return locales
    }

}