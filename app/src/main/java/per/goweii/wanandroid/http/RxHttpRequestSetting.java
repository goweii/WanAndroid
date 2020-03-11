package per.goweii.wanandroid.http;

import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;

import okhttp3.OkHttpClient;
import per.goweii.basic.core.common.Config;
import per.goweii.rxhttp.request.setting.DefaultRequestSetting;
import per.goweii.rxhttp.request.utils.HttpsCompat;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/17
 */
public class RxHttpRequestSetting extends DefaultRequestSetting {

    private final PersistentCookieJar mCookieJar;

    public RxHttpRequestSetting(PersistentCookieJar cookieJar) {
        mCookieJar = cookieJar;
    }

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
        HttpsCompat.enableTls12ForOkHttp(builder);
        HttpsCompat.enableTls12ForHttpsURLConnection();
        builder.cookieJar(mCookieJar);
    }
}
