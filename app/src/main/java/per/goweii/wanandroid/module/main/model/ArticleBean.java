package per.goweii.wanandroid.module.main.model;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class ArticleBean extends BaseBean implements MultiItemEntity {
    @Override
    public int getItemType() {
        return ArticleAdapter.ITEM_TYPE_ARTICLE;
    }

    /**
     * apkLink :
     * author : 玉刚说
     * chapterId : 410
     * chapterName : 玉刚说
     * collect : false
     * courseId : 13
     * desc :
     * envelopePic :
     * fresh : false
     * id : 8367
     * link : https://mp.weixin.qq.com/s/uI7Fej1_qSJOJnzQ6offpw
     * niceDate : 2019-05-06
     * origin :
     * prefix :
     * projectLink :
     * publishTime : 1557072000000
     * superChapterId : 408
     * superChapterName : 公众号
     * tags : [{"name":"公众号","url":"/wxarticle/list/410/1"}]
     * title : 深扒 EventBus：register
     * type : 0
     * userId : -1
     * visible : 1
     * zan : 0
     */

    private String apkLink;
    private String author;
    private String shareUser;
    private int chapterId;
    private String chapterName;
    private boolean collect;
    private int courseId;
    private String desc;
    private String envelopePic;
    private boolean top;
    private boolean fresh;
    private int id;
    private String link;
    private String niceDate;
    private String origin;
    private String prefix;
    private String projectLink;
    private long publishTime;
    private int superChapterId;
    private String superChapterName;
    private String title;
    private int type;
    private int userId;
    private int visible;
    private int zan;
    private List<TagsBean> tags;
    private int originId;

    public String getApkLink() {
        return apkLink;
    }

    public void setApkLink(String apkLink) {
        this.apkLink = apkLink;
    }

    public String getAuthor() {
        if (!TextUtils.isEmpty(author)) {
            return author;
        }
        if (!TextUtils.isEmpty(shareUser)) {
            return shareUser;
        }
        return "匿名";
    }

    public String getShareUser() {
        return shareUser;
    }

    public void setShareUser(String shareUser) {
        this.shareUser = shareUser;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEnvelopePic() {
        return envelopePic;
    }

    public void setEnvelopePic(String envelopePic) {
        this.envelopePic = envelopePic;
    }

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
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

    public String getNiceDate() {
        return niceDate;
    }

    public void setNiceDate(String niceDate) {
        this.niceDate = niceDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getProjectLink() {
        return projectLink;
    }

    public void setProjectLink(String projectLink) {
        this.projectLink = projectLink;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getSuperChapterId() {
        return superChapterId;
    }

    public void setSuperChapterId(int superChapterId) {
        this.superChapterId = superChapterId;
    }

    public String getSuperChapterName() {
        return superChapterName;
    }

    public void setSuperChapterName(String superChapterName) {
        this.superChapterName = superChapterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    public List<TagsBean> getTags() {
        return tags;
    }

    public void setTags(List<TagsBean> tags) {
        this.tags = tags;
    }

    public static class TagsBean extends BaseBean {
        /**
         * name : 公众号
         * url : /wxarticle/list/410/1
         */

        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
