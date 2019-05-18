package per.goweii.wanandroid.module.mine.presenter;

import org.greenrobot.eventbus.EventBus;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.mine.model.CollectionBean;
import per.goweii.wanandroid.module.mine.view.CollectionView;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionPresenter extends BasePresenter<CollectionView> {

    public void getCollectList(int page) {
        addToRxLife(MainRequest.getCollectList(page, new RequestListener<CollectionBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, CollectionBean data) {
                if (isAttachView()) {
                    getBaseView().getCollectListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getCollectFailed(code, msg);
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

    public void uncollect(CollectionBean.DatasBean item, final CollectView v) {
        addToRxLife(MainRequest.uncollect(item.getId(), item.getOriginId(), new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                EventBus.getDefault().post(new CollectionEvent(false, item.getOriginId()));
                if (v.isChecked()) {
                    v.toggle();
                }
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
