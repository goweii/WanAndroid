package per.goweii.wanandroid.module.main.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdvertBean {
    private String showMode;
    /**
     * [
     * 202001010000-202001020000,
     * 202001010000-202001020000
     * ]
     */
    @Nullable
    private List<String> showAtDate;
    private int duration;
    private String image;
    private String route;

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    @Nullable
    public List<String> getShowAtDate() {
        return showAtDate;
    }

    public void setShowAtDate(@Nullable List<String> showAtDate) {
        this.showAtDate = showAtDate;
    }

    public boolean shouldShowAtNow() {
        List<DateRangeEntity> list = DateRangeEntity.parse(showAtDate);
        if (list == null) {
            return true;
        }
        for (DateRangeEntity entity : list) {
            if (entity.containsNow()) {
                return true;
            }
        }
        return false;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public enum ShowMode {
        DO_NOT_SHOW,
        ONCE_OF_DAY,
        EVERY_TIME,
        ;

        @NonNull
        public static ShowMode from(String value) {
            for (ShowMode showMode : values()) {
                if (TextUtils.equals(value.toLowerCase(), showMode.name().toLowerCase())) {
                    return showMode;
                }
            }
            return DO_NOT_SHOW;
        }
    }
}
