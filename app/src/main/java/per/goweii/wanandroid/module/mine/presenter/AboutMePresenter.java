package per.goweii.wanandroid.module.mine.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.module.mine.model.AboutMeBean;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.view.AboutMeView;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AboutMePresenter extends BasePresenter<AboutMeView> {

    public void getAboutMe(){
        MineRequest.getAboutMe(getRxLife(), new RequestCallback<AboutMeBean>() {
            @Override
            public void onSuccess(int code, AboutMeBean data) {
                if (isAttachView()) {
                    getBaseView().getAboutMeSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getAboutMeFailed(code, msg);
                }
            }
        });
    }

}
