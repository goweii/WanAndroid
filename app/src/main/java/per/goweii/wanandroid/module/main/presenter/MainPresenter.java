package per.goweii.wanandroid.module.main.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.main.view.MainView;
import per.goweii.wanandroid.utils.UpdateUtils;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class MainPresenter extends BasePresenter<MainView> {

    public void update(){
        if (UpdateUtils.newInstance().isTodayChecked()) {
            return;
        }
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

}
