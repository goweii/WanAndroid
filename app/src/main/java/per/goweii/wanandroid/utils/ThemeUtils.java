package per.goweii.wanandroid.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import per.goweii.basic.utils.SPUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.MainActivity;
import per.goweii.wanandroid.module.main.activity.theme.MainActivityGold;
import per.goweii.wanandroid.module.main.activity.theme.MainActivityGreen;
import per.goweii.wanandroid.module.main.activity.theme.MainActivityPink;
import per.goweii.wanandroid.module.main.activity.theme.MainActivityRed;

public class ThemeUtils {
    private static final String THEME_NAME_RED = "red";
    private static final String THEME_NAME_GREEN = "green";
    private static final String THEME_NAME_PINK = "pink";
    private static final String THEME_NAME_GOLD = "gold";

    private static final ThemeData THEME_DEF;
    private static final List<ThemeData> THEMES = new ArrayList<>();

    private static final String SP_NAME = "launcher_theme";
    private static final String SP_KEY_WILL_INSTALL = "willInstall";
    private static SPUtils sSPUtils = null;

    static {
        THEME_DEF = new ThemeData(null, 0, MainActivity.class.getName());
        THEMES.add(THEME_DEF);
        THEMES.add(new ThemeData(THEME_NAME_RED, R.style.ThemeRed, MainActivityRed.class.getName()));
        THEMES.add(new ThemeData(THEME_NAME_GREEN, R.style.ThemeGreen, MainActivityGreen.class.getName()));
        THEMES.add(new ThemeData(THEME_NAME_PINK, R.style.ThemePink, MainActivityPink.class.getName()));
        THEMES.add(new ThemeData(THEME_NAME_GOLD, R.style.ThemeGold, MainActivityGold.class.getName()));
    }

    public static boolean isWillInstall() {
        if (sSPUtils == null) return false;
        return sSPUtils.get(SP_KEY_WILL_INSTALL, false);
    }

    public static void setWillInstall() {
        if (sSPUtils == null) sSPUtils = SPUtils.newInstance(SP_NAME);
        sSPUtils.save(SP_KEY_WILL_INSTALL, true);
    }

    public static void setNotInstall() {
        if (sSPUtils == null) sSPUtils = SPUtils.newInstance(SP_NAME);
        sSPUtils.save(SP_KEY_WILL_INSTALL, false);
    }

    @NonNull
    public static ThemeData getThemeData(String themeName) {
        if (TextUtils.isEmpty(themeName)) return THEME_DEF;
        for (ThemeData themeData : THEMES) {
            if (themeName.equals(themeData.themeName)) {
                return themeData;
            }
        }
        return THEME_DEF;
    }

    public static int getTheme(String themeName) {
        return getThemeData(themeName).themeId;
    }

    public static boolean isDefLauncher(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, THEME_DEF.activityName);
            int state = pm.getComponentEnabledSetting(componentName);
            return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void resetLauncher(Context context) {
        updateLauncher(context, null);
    }

    public static void updateLauncher(Context context, String themeName) {
        ThemeData newThemeData = getThemeData(themeName);
        PackageManager pm = context.getPackageManager();
        for (ThemeData themeData : THEMES) {
            try {
                ComponentName componentName = new ComponentName(context, themeData.activityName);
                int state = pm.getComponentEnabledSetting(componentName);
                if (!TextUtils.equals(themeData.activityName, newThemeData.activityName)) {
                    if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                        pm.setComponentEnabledSetting(componentName,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            ComponentName componentName = new ComponentName(context, newThemeData.activityName);
            int state = pm.getComponentEnabledSetting(componentName);
            if (state != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                pm.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ThemeData {
        public final String themeName;
        public final int themeId;
        public final String activityName;

        public ThemeData(String themeName, int themeId, String activityName) {
            this.themeName = themeName;
            this.themeId = themeId;
            this.activityName = activityName;
        }
    }
}
