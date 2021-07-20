package per.goweii.wanandroid.http;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import per.goweii.basic.core.common.Config;
import per.goweii.basic.utils.DebugUtils;
import per.goweii.rxhttp.request.setting.DefaultRequestSetting;
import per.goweii.rxhttp.request.setting.ParameterGetter;
import per.goweii.rxhttp.request.utils.HttpsCompat;
import per.goweii.wanandroid.utils.UserUtils;

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
        return DebugUtils.isDebug();
    }

    @Override
    public long getTimeout() {
        return Config.HTTP_TIMEOUT;
    }

    @NonNull
    @Override
    public String getBaseUrl() {
        return WanApi.ApiConfig.BASE_URL;
    }

    @Nullable
    @Override
    public Map<Class<?>, String> getServiceBaseUrl() {
        Map<Class<?>, String> map = new HashMap<>(1);
        if (CmsApi.Companion.isEnabled()) {
            map.put(CmsApi.ApiService.class, CmsApi.ApiConfig.BASE_URL);
        }
        return map;
    }

    @Nullable
    @Override
    public Map<String, String> getStaticHeaderParameter() {
        Map<String, String> map = new HashMap<>(1);
        map.put("HeaderParameterHolder", "Nothing");
        return map;
    }

    @Nullable
    @Override
    public Map<String, ParameterGetter> getDynamicHeaderParameter() {
        Map<String, ParameterGetter> map = new HashMap<>(2);
        if (!TextUtils.isEmpty(UserUtils.getInstance().getCmsJwt())) {
            map.put("Authorization", new ParameterGetter() {
                @Override
                public String get() {
                    return "Bearer " + UserUtils.getInstance().getCmsJwt();
                }
            });
        }
        return map;
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
