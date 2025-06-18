package per.goweii.basic.l10n;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public class L10nManager {
    public static  void setApplicationLocales(@Nullable String languageCode) {
        LocaleListCompat appLocale;
        if (languageCode == null || languageCode.isEmpty()) {
            appLocale = LocaleListCompat.getEmptyLocaleList();
        } else {
            appLocale = LocaleListCompat.forLanguageTags(languageCode);
        }
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}
