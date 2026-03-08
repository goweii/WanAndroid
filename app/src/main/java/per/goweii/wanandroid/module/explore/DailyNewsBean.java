package per.goweii.wanandroid.module.explore;

import java.util.Objects;

public class DailyNewsBean {
    private String title;
    private String url;
    private String content;
    private String source;
    private String publish_time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DailyNewsBean that = (DailyNewsBean) o;
        return Objects.equals(title, that.title) && Objects.equals(url, that.url) && Objects.equals(content, that.content) && Objects.equals(source, that.source) && Objects.equals(publish_time, that.publish_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, content, source, publish_time);
    }
}
