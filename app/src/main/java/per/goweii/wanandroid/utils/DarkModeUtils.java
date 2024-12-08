package per.goweii.wanandroid.utils;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * @author CuiZhen
 * @date 2020/2/17
 * GitHub: https://github.com/goweii
 */
public class DarkModeUtils {
    public static boolean isDarkMode(Configuration config) {
        int uiMode = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return uiMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isDarkMode(Context context) {
        return isDarkMode(context.getResources().getConfiguration());
    }

    public static void initDarkMode() {
        switch (SettingUtils.getInstance().getThemeMode()) {
            case FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
