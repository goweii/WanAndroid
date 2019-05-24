package per.goweii.wanandroid.module.knowledge.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgeRequest extends BaseRequest {

    public static void getKnowledgeList(RxLife rxLife, @NonNull RequestListener<List<KnowledgeBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getKnowledgeList(),
                WanCache.CacheKey.KNOWLEDGE_LIST,
                KnowledgeBean.class,
                listener);
    }

    public static void getKnowledgeArticleList(RxLife rxLife, int id, @IntRange(from = 0) int page, @NonNull RequestListener<KnowledgeArticleBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getKnowledgeArticleList(page, id),
                WanCache.CacheKey.KNOWLEDGE_ARTICLE_LIST(id, page),
                KnowledgeArticleBean.class,
                listener);
    }

}
