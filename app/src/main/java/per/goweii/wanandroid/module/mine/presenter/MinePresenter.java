package per.goweii.wanandroid.module.mine.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.model.UserInfoBean;
import per.goweii.wanandroid.module.mine.view.MineView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class MinePresenter extends BasePresenter<MineView> {

    public void getUserInfo() {
        addToRxLife(MineRequest.getUserInfo(new RequestListener<UserInfoBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, UserInfoBean data) {
                if (isAttach()) {
                    getBaseView().getUserInfoSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getUserInfoFail(code, msg);
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
