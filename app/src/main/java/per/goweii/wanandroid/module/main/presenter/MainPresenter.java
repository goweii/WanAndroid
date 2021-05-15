package per.goweii.wanandroid.module.main.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.db.executor.ReadLaterExecutor;
import per.goweii.wanandroid.db.model.ReadLaterModel;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.AdvertBean;
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

    private ReadLaterExecutor mReadLaterExecutor = null;

    @Override
    public void attach(MainView baseView) {
        super.attach(baseView);
        mReadLaterExecutor = new ReadLaterExecutor();
    }

    @Override
    public void detach() {
        if (mReadLaterExecutor != null) mReadLaterExecutor.destroy();
        super.detach();
    }

    public void update() {
        MainRequest.update(getRxLife(), new RequestCallback<UpdateBean>() {
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
        });
    }

    public void betaUpdate() {
        MainRequest.betaUpdate(getRxLife(), new RequestCallback<UpdateBean>() {
            @Override
            public void onSuccess(int code, UpdateBean data) {
                if (isAttach()) {
                    getBaseView().betaUpdateSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().betaUpdateFailed(code, msg);
                }
            }
        });
    }

    public void getConfig() {
        MainRequest.getConfig(getRxLife(), new RequestListener<ConfigBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, ConfigBean data) {
                if (isAttach()) {
                    getBaseView().getConfigSuccess(data);
                }
                int oldTheme = ConfigUtils.getInstance().getTheme();
                ConfigUtils.getInstance().setConfig(data);
                int newTheme = ConfigUtils.getInstance().getTheme();
                if (oldTheme != newTheme) {
                    if (isAttach()) {
                        getBaseView().newThemeFounded();
                    }
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getConfigFailed(code, msg);
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

    public void getAdvert() {
        MainRequest.getAdvert(getRxLife(), new RequestCallback<AdvertBean>() {
            @Override
            public void onSuccess(int code, AdvertBean data) {
                if (isAttach()) {
                    getBaseView().getAdvertSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getAdvertFailed(code, msg);
                }
            }
        });
    }

    public void getReadLaterArticle() {
        mReadLaterExecutor.findLately(1, new SimpleCallback<List<ReadLaterModel>>() {
            @Override
            public void onResult(List<ReadLaterModel> data) {
                if (!data.isEmpty()) {
                    if (isAttach()) {
                        getBaseView().getReadLaterArticleSuccess(data.get(0));
                    }
                } else {
                    if (isAttach()) {
                        getBaseView().getReadLaterArticleFailed();
                    }
                }
            }
        }, new SimpleListener() {
            @Override
            public void onResult() {
                if (isAttach()) {
                    getBaseView().getReadLaterArticleFailed();
                }
            }
        });
    }

}
