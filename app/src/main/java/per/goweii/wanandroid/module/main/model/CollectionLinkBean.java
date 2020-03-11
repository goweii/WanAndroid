package per.goweii.wanandroid.module.main.model;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public class CollectionLinkBean extends BaseBean {
    /**
     * desc :
     * icon :
     * id : 1542
     * link : https://www.baidu.com/
     * name : baidu
     * order : 0
     * userId : 20382
     * visible : 1
     */

    private String desc;
    private String icon;
    private int id;
    private String link;
    private String name;
    private int order;
    private int userId;
    private int visible;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }
}
