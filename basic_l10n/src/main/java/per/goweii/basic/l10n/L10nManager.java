package per.goweii.basic.l10n;

import android.content.Context;
import android.content.res.XmlResourceParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class L10nManager {
    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";

    public static void setApplicationLocales(@Nullable String languageCode) {
        LocaleListCompat appLocale;
        if (languageCode == null || languageCode.isEmpty()) {
            appLocale = LocaleListCompat.getEmptyLocaleList();
        } else {
            appLocale = LocaleListCompat.forLanguageTags(languageCode);
        }
        AppCompatDelegate.setApplicationLocales(appLocale);
    }

    public static void setApplicationLocales(@Nullable Locale locale) {
        LocaleListCompat appLocale;
        if (locale == null) {
            appLocale = LocaleListCompat.getEmptyLocaleList();
        } else {
            appLocale = LocaleListCompat.create(locale);
        }
        AppCompatDelegate.setApplicationLocales(appLocale);
    }

    @NonNull
    public static List<Locale> getSupportedLocales(@NonNull Context context) {
        try (XmlResourceParser parser = context.getResources().getXml(R.xml.locale_config)) {
            final List<Locale> locales = new ArrayList<>();
            int event = parser.next();
            while (event != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    if ("locale".equals(parser.getName())) {
                        String name = parser.getAttributeValue(ANDROID_NAMESPACE, "name");
                        locales.add(Locale.forLanguageTag(name));
                    }
                }
                event = parser.next();
            }
            return locales;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
