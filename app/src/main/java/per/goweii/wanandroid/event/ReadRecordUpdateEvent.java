package per.goweii.wanandroid.event;

import io.reactivex.annotations.NonNull;

public class ReadRecordUpdateEvent extends BaseEvent {
    private final String mLink;
    private String mTitle;
    private Long mTime;
    private Float mPercent;

    public ReadRecordUpdateEvent(@NonNull String link) {
        this.mLink = link;
    }

    public String getLink() {
        return mLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Long getTime() {
        return mTime;
    }

    public void setTime(Long time) {
        this.mTime = time;
    }

    public Float getPercent() {
        return mPercent;
    }

    public void setPercent(Float percent) {
        this.mPercent = percent;
    }
}
