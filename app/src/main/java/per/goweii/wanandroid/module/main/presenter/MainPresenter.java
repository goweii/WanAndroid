package per.goweii.wanandroid.module.main.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.utils.LogUtils;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.main.view.MainView;
import per.goweii.wanandroid.utils.ConfigUtils;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public class MainPresenter extends BasePresenter<MainView> {

    public void update(){
        MainRequest.update(getRxLife(), new RequestCallback<UpdateBean>() {
            @Override
            public void onSuccess(int code, UpdateBean data) {
                if (isAttach()) {
                    getBaseView().updateSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    public void getConfig() {
        ConfigBean configBean = ConfigUtils.getInstance().getConfig();
        LogUtils.i("ConfigBean", "getConfig" + configBean.toFormatJson());
        getBaseView().getConfigSuccess(configBean);
        LogUtils.i("ConfigBean", ConfigUtils.getInstance().isTodayUpdate());
        if (ConfigUtils.getInstance().isTodayUpdate()) {
            return;
        }
        MainRequest.getConfig(getRxLife(), new RequestListener<ConfigBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, ConfigBean data) {
                LogUtils.i("ConfigBean", "onSuccess" + data.toFormatJson());
                getBaseView().getConfigSuccess(data);
                ConfigUtils.getInstance().setConfig(data);
            }

            @Override
            public void onFailed(int code, String msg) {
                LogUtils.i("ConfigBean", "onFailed");
            }

            @Override
            public void onError(ExceptionHandle handle) {
                LogUtils.i("ConfigBean", "onError" + handle.getException().toString());
            }

            @Override
            public void onFinish() {
            }
        });
    }

}
