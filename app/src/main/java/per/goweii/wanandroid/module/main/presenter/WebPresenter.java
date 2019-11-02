package per.goweii.wanandroid.module.main.presenter;

import android.text.TextUtils;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.CollectArticleEntity;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.view.WebView;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebPresenter extends BasePresenter<WebView> {

    public void collect(CollectArticleEntity entity) {
        if (entity.isCollect()) {
            if (isAttach()) {
                getBaseView().collectSuccess(entity);
            }
            return;
        }
        if (entity.getArticleId() > 0) {
            collect(entity.getArticleId(), entity);
        } else {
            if (TextUtils.isEmpty(entity.getAuthor())) {
                collect(entity.getTitle(), entity.getUrl(), entity);
            } else {
                collect(entity.getTitle(), entity.getAuthor(), entity.getUrl(), entity);
            }
        }
    }

    public void uncollect(CollectArticleEntity entity) {
        if (!entity.isCollect()) {
            if (isAttach()) {
                getBaseView().uncollectSuccess(entity);
            }
            return;
        }
        if (entity.getArticleId() > 0) {
            uncollect(entity.getArticleId(), entity);
        } else {
            uncollectLink(entity.getCollectId(), entity);
        }
    }

    private void collect(int id, final CollectArticleEntity entity) {
        addToRxLife(MainRequest.collect(id, new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postCollectWithArticleId(id);
                entity.setCollect(true);
                if (isAttach()) {
                    getBaseView().collectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().collectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        }));
    }

    private void uncollect(final int id, final CollectArticleEntity entity) {
        addToRxLife(MainRequest.uncollect(id, new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postUnCollectWithArticleId(id);
                entity.setCollect(false);
                if (isAttach()) {
                    getBaseView().uncollectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().uncollectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        }));
    }

    private void collect(String title, String author, String link, CollectArticleEntity entity) {
        addToRxLife(MainRequest.collect(title, author, link, new RequestListener<ArticleBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, ArticleBean data) {
                CollectionEvent.postCollectWithCollectId(data.getId());
                entity.setCollect(true);
                if (isAttach()) {
                    getBaseView().collectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().collectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        }));
    }

    private void collect(String title, String link, CollectArticleEntity entity) {
        addToRxLife(MainRequest.collect(title, link, new RequestListener<CollectionLinkBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, CollectionLinkBean data) {
                CollectionEvent.postCollectWithCollectId(data.getId());
                entity.setCollect(true);
                if (isAttach()) {
                    getBaseView().collectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().collectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        }));
    }

    private void uncollectLink(int id, CollectArticleEntity entity) {
        addToRxLife(MainRequest.uncollectLink(id, new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postUncollectWithCollectId(id);
                entity.setCollect(false);
                if (isAttach()) {
                    getBaseView().uncollectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().uncollectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        }));
    }
}
