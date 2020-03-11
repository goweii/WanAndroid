package per.goweii.wanandroid.module.navigation.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.navigation.model.NaviBean;
import per.goweii.wanandroid.module.navigation.model.NaviRequest;
import per.goweii.wanandroid.module.navigation.view.NaviView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class NaviPresenter extends BasePresenter<NaviView> {

    public void getKnowledgeListCache() {
        NaviRequest.getNaviListCache(new RequestListener<List<NaviBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<NaviBean> data) {
                if (isAttach()) {
                    getBaseView().getNaviListSuccess(code, data);
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

    public void getKnowledgeList() {
        NaviRequest.getNaviList(getRxLife(), new RequestListener<List<NaviBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<NaviBean> data) {
                if (isAttach()) {
                    getBaseView().getNaviListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getNaviListFail(code, msg);
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
}
