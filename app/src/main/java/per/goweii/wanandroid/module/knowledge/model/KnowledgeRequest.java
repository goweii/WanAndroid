package per.goweii.wanandroid.module.knowledge.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import per.goweii.wanandroid.http.BaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.http.WanApi;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgeRequest extends BaseRequest {

    public static Disposable getKnowledgeList(@NonNull RequestListener<List<KnowledgeBean>> listener) {
        return request(WanApi.api().getKnowledgeList(), listener);
    }

    public static Disposable getKnowledgeArticleList(int id, @IntRange(from = 0) int page, @NonNull RequestListener<KnowledgeArticleBean> listener) {
        return request(WanApi.api().getKnowledgeArticleList(page, id), listener);
    }

}
