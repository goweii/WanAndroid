package per.goweii.wanandroid.module.mine.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.LocaleManagerCompat;
import androidx.core.os.LocaleListCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Locale;

import per.goweii.wanandroid.R;

public class LanguageAdapter extends BaseQuickAdapter<Locale, BaseViewHolder> {
    @Nullable
    private final Locale mAppLocale;
    @Nullable
    private final Locale mSysLocale;

    public LanguageAdapter(@Nullable Locale appLocale, @Nullable Locale sysLocale) {
        super(R.layout.rv_item_language);
        this.mAppLocale = appLocale;
        this.mSysLocale = sysLocale;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, @Nullable Locale item) {
        if (item == null) {
            helper.setText(R.id.tv_title, R.string.follow_the_system);
            if (mSysLocale == null) {
                helper.setVisible(R.id.tv_subtitle, false);
            } else {
                helper.setVisible(R.id.tv_subtitle, true);
                helper.setText(R.id.tv_subtitle, mSysLocale.getDisplayLanguage(mSysLocale));
            }
            helper.setVisible(R.id.iv_checked, mAppLocale == null);
        } else {
            helper.setText(R.id.tv_title, item.getDisplayLanguage());
            helper.setText(R.id.tv_subtitle, item.getDisplayLanguage(item));
            helper.setVisible(R.id.iv_checked, item.equals(mAppLocale));
        }
    }
}
