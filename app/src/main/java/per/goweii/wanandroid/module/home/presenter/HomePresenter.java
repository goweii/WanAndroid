package per.goweii.wanandroid.module.home.presenter;

import androidx.annotation.IntRange;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.home.model.HomeRequest;
import per.goweii.wanandroid.module.home.view.HomeView;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class HomePresenter extends BasePresenter<HomeView> {

    private boolean isGetBannerSuccess = false;
    private boolean isGetArticleListSuccess = false;
    private boolean isGetTopArticleListSuccess = false;

    private void isAllFailed() {
        if (!isGetBannerSuccess && !isGetArticleListSuccess && !isGetTopArticleListSuccess) {
            if (isAttach()) {
                getBaseView().allFail();
            }
        }
    }

    public void getBanner() {
        HomeRequest.getBanner(getRxLife(), new RequestListener<List<BannerBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<BannerBean> data) {
                isGetBannerSuccess = true;
                if (isAttach()) {
                    getBaseView().getBannerSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getBannerFail(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                isAllFailed();
            }
        });
    }

    public void getArticleList(@IntRange(from = 0) int page, boolean refresh) {
        HomeRequest.getArticleList(getRxLife(), refresh, page, new RequestListener<ArticleListBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, ArticleListBean data) {
                isGetArticleListSuccess = true;
                if (isAttach()) {
                    getBaseView().getArticleListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getArticleListFailed(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                isAllFailed();
            }
        });
    }

    public void getTopArticleList(boolean refresh) {
        HomeRequest.getTopArticleList(getRxLife(), refresh, new RequestListener<List<ArticleBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ArticleBean> data) {
                isGetTopArticleListSuccess = true;
                if (isAttach()) {
                    getBaseView().getTopArticleListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getTopArticleListFailed(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                isAllFailed();
            }
        });
    }

    public void collect(ArticleBean item, final CollectView v) {
        addToRxLife(MainRequest.collect(item.getId(), new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                item.setCollect(true);
                if (!v.isChecked()) {
                    v.toggle();
                }
                CollectionEvent.postCollectWithArticleId(item.getId());
            }

            @Override
            public void onFailed(int code, String msg) {
                if (v.isChecked()) {
                    v.toggle();
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

    public void uncollect(ArticleBean item, final CollectView v) {
        addToRxLife(MainRequest.uncollect(item.getId(), new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                item.setCollect(false);
                if (v.isChecked()) {
                    v.toggle();
                }
                CollectionEvent.postUnCollectWithArticleId(item.getId());
            }

            @Override
            public void onFailed(int code, String msg) {
                if (!v.isChecked()) {
                    v.toggle();
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
