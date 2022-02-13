package per.goweii.wanandroid.utils

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl

object CookieUtils {
    lateinit var cookieJar: PersistentCookieJar

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(SetCookieCache(),
                SharedPrefsCookiePersistor(context.applicationContext))
    }

    fun loadForUrl(url: String): MutableList<Cookie> {
        if (url.isBlank()) {
            return arrayListOf()
        }
        if (!this::cookieJar.isInitialized) {
            return arrayListOf()
        }
        return cookieJar.loadForRequest(HttpUrl.get(url))
    }
}