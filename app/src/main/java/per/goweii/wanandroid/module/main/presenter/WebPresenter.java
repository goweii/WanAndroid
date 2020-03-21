package per.goweii.wanandroid.module.main.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.bitmap.BitmapUtils;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.db.executor.ReadLaterExecutor;
import per.goweii.wanandroid.db.executor.ReadRecordExecutor;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.event.ReadRecordEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.CollectArticleEntity;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.view.WebView;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public class WebPresenter extends BasePresenter<WebView> {

    private ReadLaterExecutor mReadLaterExecutor = null;
    private ReadRecordExecutor mReadRecordExecutor = null;

    @Override
    public void attach(WebView baseView) {
        super.attach(baseView);
        mReadLaterExecutor = new ReadLaterExecutor();
        mReadRecordExecutor = new ReadRecordExecutor();
    }

    @Override
    public void detach() {
        if (mReadLaterExecutor != null) mReadLaterExecutor.destroy();
        if (mReadRecordExecutor != null) mReadRecordExecutor.destroy();
        super.detach();
    }

    public void collect(CollectArticleEntity entity) {
        if (entity.isCollect()) {
            if (isAttach()) {
                getBaseView().collectSuccess(entity);
            }
            return;
        }
        if (entity.getArticleId() > 0) {
            collectArticle(entity.getArticleId(), entity);
        } else {
            if (TextUtils.isEmpty(entity.getAuthor())) {
                collectLink(entity.getTitle(), entity.getUrl(), entity);
            } else {
                collectArticle(entity.getTitle(), entity.getAuthor(), entity.getUrl(), entity);
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
            uncollectArticle(entity.getArticleId(), entity);
        } else {
            uncollectLink(entity.getCollectId(), entity);
        }
    }

    private void collectArticle(int id, final CollectArticleEntity entity) {
        addToRxLife(MainRequest.collectArticle(id, new RequestListener<BaseBean>() {
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

    private void uncollectArticle(final int id, final CollectArticleEntity entity) {
        addToRxLife(MainRequest.uncollectArticle(id, new RequestListener<BaseBean>() {
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

    private void collectArticle(String title, String author, String link, CollectArticleEntity entity) {
        addToRxLife(MainRequest.collectArticle(title, author, link, new RequestListener<ArticleBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, ArticleBean data) {
                CollectionEvent.postCollectWithCollectId(data.getId());
                entity.setArticleId(data.getId());
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

    private void collectLink(String title, String link, CollectArticleEntity entity) {
        addToRxLife(MainRequest.collectLink(title, link, new RequestListener<CollectionLinkBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, CollectionLinkBean data) {
                CollectionEvent.postCollectWithCollectId(data.getId());
                entity.setCollectId(data.getId());
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

    public void saveGallery(Bitmap bitmap, String name) {
        if (null != BitmapUtils.saveGallery(bitmap, name)) {
            ToastMaker.showShort("以保存到相册");
        } else {
            ToastMaker.showShort("保存失败");
        }
        bitmap.recycle();
    }

    public void readLater(String link, String title) {
        if (mReadLaterExecutor == null) return;
        mReadLaterExecutor.add(link, title, new SimpleListener() {
            @Override
            public void onResult() {
                ToastMaker.showShort("已加入稍后阅读");
            }
        }, new SimpleListener() {
            @Override
            public void onResult() {
                ToastMaker.showShort("加入稍后阅读失败");
            }
        });
    }

    public void readRecord(String link, String title) {
        if (mReadRecordExecutor == null) return;
        if (!SettingUtils.getInstance().isShowReadRecord()) {
            return;
        }
        if (TextUtils.isEmpty(link)) {
            return;
        }
        if (TextUtils.isEmpty(title)) {
            return;
        }
        if (TextUtils.equals(link, title)) {
            return;
        }
        mReadRecordExecutor.add(link, title, new SimpleListener() {
            @Override
            public void onResult() {
                new ReadRecordEvent().post();
            }
        }, new SimpleListener() {
            @Override
            public void onResult() {
            }
        });
    }
}
