package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class CollectionEvent extends BaseEvent {

    private boolean collect;
    private int articleId;
    private int collectId;

    public static void postCollectWithCollectId(int collectId){
        new CollectionEvent(true, -1, collectId).post();
    }

    public static void postCollectWithArticleId(int articleId){
        new CollectionEvent(true, articleId, -1).post();
    }

    public static void postUnCollectWithArticleId(int articleId){
        new CollectionEvent(false, articleId, -1).post();
    }

    public static void postUncollectWithCollectId(int collectId){
        new CollectionEvent(false, -1, collectId).post();
    }

    public static void postUncollect(int articleId, int collectId){
        new CollectionEvent(false, articleId, collectId).post();
    }

    private CollectionEvent(boolean collect, int articleId, int collectId) {
        this.collect = collect;
        this.articleId = articleId;
        this.collectId = collectId;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getCollectId() {
        return collectId;
    }

    public void setCollectId(int collectId) {
        this.collectId = collectId;
    }
}
