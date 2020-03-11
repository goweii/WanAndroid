package per.goweii.wanandroid.module.knowledge.model;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.ChapterBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class KnowledgeRequest extends BaseRequest {

    public static void getKnowledgeListCache(@NonNull RequestListener<List<ChapterBean>> listener) {
        cacheList(WanCache.CacheKey.KNOWLEDGE_LIST,
                ChapterBean.class,
                listener);
    }

    public static void getKnowledgeList(RxLife rxLife, @NonNull RequestListener<List<ChapterBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getKnowledgeList(),
                true,
                WanCache.CacheKey.KNOWLEDGE_LIST,
                ChapterBean.class,
                listener);
    }

    public static void getKnowledgeArticleListCache(int id, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        cacheBean(WanCache.CacheKey.KNOWLEDGE_ARTICLE_LIST(id, page),
                ArticleListBean.class,
                listener);
    }

    public static void getKnowledgeArticleList(RxLife rxLife, boolean refresh, int id, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 0) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getKnowledgeArticleList(page, id),
                    refresh,
                    WanCache.CacheKey.KNOWLEDGE_ARTICLE_LIST(id, page),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getKnowledgeArticleList(page, id), listener));
        }
    }

}
