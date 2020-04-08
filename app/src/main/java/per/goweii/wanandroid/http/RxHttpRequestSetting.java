package per.goweii.wanandroid.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import per.goweii.basic.core.common.Config;
import per.goweii.basic.utils.LogUtils;
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
    private final Gson mGson;

    public RxHttpRequestSetting(PersistentCookieJar cookieJar) {
        mCookieJar = cookieJar;
        mGson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public boolean isDebug() {
        return false;
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

    @Nullable
    @Override
    public Interceptor[] getNetworkInterceptors() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                String msg;
                try {
                    msg = mGson.toJson(JsonParser.parseString(message));
                } catch (Exception e) {
                    msg = message;
                }
                LogUtils.i("HttpLoggingInterceptor", msg);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new Interceptor[]{httpLoggingInterceptor};
    }

    @Override
    public void setOkHttpClient(OkHttpClient.Builder builder) {
        super.setOkHttpClient(builder);
        HttpsCompat.enableTls12ForOkHttp(builder);
        HttpsCompat.enableTls12ForHttpsURLConnection();
        builder.cookieJar(mCookieJar);
    }
}
