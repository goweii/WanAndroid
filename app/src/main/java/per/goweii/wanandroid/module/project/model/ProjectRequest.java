package per.goweii.wanandroid.module.project.model;

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
public class ProjectRequest extends BaseRequest {

    public static Disposable getProjectChapters(@NonNull RequestListener<List<ProjectChapterBean>> listener) {
        return request(WanApi.api().getProjectChapters(), listener);
    }

    public static Disposable getProjectArticleList(int id, @IntRange(from = 1) int page, @NonNull RequestListener<ProjectArticleBean> listener) {
        return request(WanApi.api().getProjectArticleList(page, id), listener);
    }

}
