package per.goweii.basic.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.UUID;

import per.goweii.basic.utils.coder.MD5Coder;

/**
 * 获取设备号
 *
 * @author Cuizhen
 * @date 2018/6/30-上午10:30
 */
public class DeviceIdUtils {

    private static final String KEY_ID = "id";
    private static final String SP_NAME = "device_id";

    @SuppressLint("HardwareIds")
    @NonNull
    public static String getId() {
        synchronized (DeviceIdUtils.class) {
            SharedPreferences sp = Utils.getAppContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            String deviceId = sp.getString(KEY_ID, null);
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId;
            }
            String id = DeviceUtils.getAndroidId() + DeviceUtils.getSerial();
            if (TextUtils.isEmpty(id)) {
                id = UUID.randomUUID().toString();
            }
            deviceId = MD5Coder.encode(id);
            sp.edit().putString(KEY_ID, deviceId).apply();
            return deviceId;
        }
    }
}
