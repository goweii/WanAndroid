package per.goweii.wanandroid.module.mine.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.mine.view.AboutView;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public class AboutPresenter extends BasePresenter<AboutView> {

    public void getAppDownloadUrl() {
        MainRequest.update(getRxLife(), new RequestListener<UpdateBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, UpdateBean data) {
                if (isAttach()) {
                    getBaseView().updateSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().updateFailed(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        });
    }

}
