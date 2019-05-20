package per.goweii.wanandroid.module.wxarticle.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.wxarticle.model.WxArticleBean;
import per.goweii.wanandroid.module.wxarticle.model.WxRequest;
import per.goweii.wanandroid.module.wxarticle.view.WxArticleView;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WxArticlePresenter extends BasePresenter<WxArticleView> {

    public void getWxArticleList(int id, int page){
        addToRxLife(WxRequest.getWxArticleList(id, page, new RequestListener<WxArticleBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, WxArticleBean data) {
                if (isAttachView()) {
                    getBaseView().getWxArticleListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getWxArticleListFailed(code, msg);
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

    public void getWxArticleListSearch(int id, int page, String key){
        addToRxLife(WxRequest.getWxArticleList(id, page, key, new RequestListener<WxArticleBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, WxArticleBean data) {
                if (isAttachView()) {
                    getBaseView().getWxArticleListSearchSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getWxArticleListSearchFailed(code, msg);
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

    public void collect(WxArticleBean.DatasBean item, final CollectView v){
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

    public void uncollect(WxArticleBean.DatasBean item, final CollectView v){
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
