package per.goweii.wanandroid.event;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class ArticleDeleteEvent extends BaseEvent {

    private int articleId;

    public static void postWithArticleId(int articleId) {
        new ArticleDeleteEvent(articleId).post();
    }

    private ArticleDeleteEvent(int articleId) {
        this.articleId = articleId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}
