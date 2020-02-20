package per.goweii.wanandroid.module.mine.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.view.CollectionLinkView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class CollectionLinkPresenter extends BasePresenter<CollectionLinkView> {

    public void getCollectLinkListCache() {
        MineRequest.getCollectLinkListCache(new RequestListener<List<CollectionLinkBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<CollectionLinkBean> data) {
                if (isAttach()) {
                    getBaseView().getCollectLinkListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    public void getCollectLinkList(boolean refresh) {
        MineRequest.getCollectLinkList(getRxLife(), refresh, new RequestListener<List<CollectionLinkBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<CollectionLinkBean> data) {
                if (isAttach()) {
                    getBaseView().getCollectLinkListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getCollectLinkListFailed(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    public void uncollectLink(CollectionLinkBean item) {
        addToRxLife(MainRequest.uncollectLink(item.getId(), new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postUncollectWithCollectId(item.getId());
            }

            @Override
            public void onFailed(int code, String msg) {
                ToastMaker.showShort(msg);
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        }));
    }

    public void updateCollectLink(CollectionLinkBean item) {
        addToRxLife(MineRequest.updateCollectLink(item.getId(), item.getName(), item.getLink(), new RequestListener<CollectionLinkBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, CollectionLinkBean data) {
                if (isAttach()) {
                    getBaseView().updateCollectLinkSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                ToastMaker.showShort(msg);
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
