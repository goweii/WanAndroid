package per.goweii.wanandroid.module.home.presenter;

import android.support.annotation.IntRange;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.home.model.HomeBean;
import per.goweii.wanandroid.module.home.model.HomeRequest;
import per.goweii.wanandroid.module.home.view.HomeView;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class HomePresenter extends BasePresenter<HomeView> {

    public void getBanner(){
        addToRxLife(HomeRequest.getBanner(new RequestListener<List<BannerBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<BannerBean> data) {
                if (isAttachView()) {
                    getBaseView().getBannerSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getBannerFail(code, msg);
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

    public void getArticleList(@IntRange(from = 0) int page){
        addToRxLife(HomeRequest.getArticleList(page, new RequestListener<HomeBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, HomeBean data) {
                if (isAttachView()) {
                    getBaseView().getArticleListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getArticleListFailed(code, msg);
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

    public void getTopArticleList(){
        addToRxLife(HomeRequest.getTopArticleList(new RequestListener<List<ArticleBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ArticleBean> data) {
                if (isAttachView()) {
                    getBaseView().getTopArticleListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getTopArticleListFailed(code, msg);
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

    public void collect(ArticleBean item, final CollectView v){
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

    public void uncollect(ArticleBean item, final CollectView v){
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
