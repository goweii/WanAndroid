package per.goweii.wanandroid.http;

import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;
import per.goweii.basic.core.common.Config;
import per.goweii.basic.utils.Utils;
import per.goweii.rxhttp.request.setting.DefaultRequestSetting;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/17
 */
public class RxHttpRequestSetting extends DefaultRequestSetting {

    @Override
    public boolean isDebug() {
        return Config.DEBUG;
    }

    @Override
    public long getTimeout() {
        return Config.HTTP_TIMEOUT;
    }

    @NonNull
    @Override
    public String getBaseUrl() {
        return Config.BASE_URL;
    }

    @Override
    public int getSuccessCode() {
        return 0;
    }

    @Override
    public void setOkHttpClient(OkHttpClient.Builder builder) {
        super.setOkHttpClient(builder);
        builder.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Utils.getAppContext())));
    }
}
