package per.goweii.wanandroid.module.mine.model;

import per.goweii.basic.core.base.BaseEntity;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * GitHub: https://github.com/goweii
 */
public class OpenEntity extends BaseEntity {

    private String project;
    private String description;
    private String link;

    public OpenEntity(String project, String description, String link) {
        this.project = project;
        this.description = description;
        this.link = link;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
