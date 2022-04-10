package per.goweii.wanandroid.module.book.model;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;
import per.goweii.wanandroid.http.WanCache;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeRequest;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.ChapterBean;

public class BookRequest extends BaseRequest {
    public static void getBookList(RxLife rxLife, @NonNull RequestListener<List<BookBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getBooks(),
                false,
                WanCache.CacheKey.BOOK_LIST,
                BookBean.class,
                listener);
    }

    public static void getBookChapterList(RxLife rxLife, int id, @IntRange(from = 0) int page, @NonNull RequestListener<ArticleListBean> listener) {
        if (page == 0) {
            cacheAndNetBean(rxLife,
                    WanApi.api().getChapterArticleList(page, id, 1),
                    false,
                    WanCache.CacheKey.CHAPTER_ARTICLE_LIST(id, page, 1),
                    ArticleListBean.class,
                    listener);
        } else {
            rxLife.add(request(WanApi.api().getChapterArticleList(page, id, 1), listener));
        }
    }

}
