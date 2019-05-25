package per.goweii.wanandroid.module.project.model;

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
public class ProjectRequest extends BaseRequest {

    public static void getProjectChapters(RxLife rxLife, @NonNull RequestListener<List<ProjectChapterBean>> listener) {
        cacheAndNetList(rxLife,
                WanApi.api().getProjectChapters(),
                WanCache.CacheKey.PROJECT_CHAPTERS,
                ProjectChapterBean.class,
                listener);
    }

    public static void getProjectArticleList(RxLife rxLife, boolean refresh, int id, @IntRange(from = 1) int page, @NonNull RequestListener<ProjectArticleBean> listener) {
        cacheAndNetBean(rxLife,
                WanApi.api().getProjectArticleList(page, id),
                refresh,
                WanCache.CacheKey.PROJECT_ARTICLE_LIST(id, page),
                ProjectArticleBean.class,
                listener);
    }

}
