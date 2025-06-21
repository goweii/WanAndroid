package per.goweii.wanandroid.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

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

    public static void initDarkMode(Context context) {
        SettingUtils.ThemeMode themeMode = SettingUtils.getInstance().getThemeMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            switch (themeMode) {
                case FOLLOW_SYSTEM:
                    uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO);
                    break;
                case LIGHT:
                    uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO);
                    break;
                case DARK:
                    uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES);
                    break;
            }
        }
        switch (themeMode) {
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
