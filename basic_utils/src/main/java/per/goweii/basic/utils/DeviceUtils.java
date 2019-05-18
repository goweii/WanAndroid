package per.goweii.basic.utils;

import android.annotation.SuppressLint;
import android.provider.Settings;

/**
 * 获取设备号
 *
 * @author Cuizhen
 * @date 2018/6/30-上午10:30
 */
public class DeviceUtils {

    @SuppressLint("HardwareIds")
    public static String getAndroidId() {
        return Settings.Secure.getString(Utils.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getSerial(){
        return android.os.Build.SERIAL;
    }

}
