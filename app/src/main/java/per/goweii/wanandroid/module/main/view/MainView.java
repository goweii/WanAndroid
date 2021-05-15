package per.goweii.wanandroid.module.main.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.db.model.ReadLaterModel;
import per.goweii.wanandroid.module.main.model.AdvertBean;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.module.main.model.UpdateBean;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public interface MainView extends BaseView {
    void updateSuccess(int code, UpdateBean data);

    void updateFailed(int code, String msg);

    void betaUpdateSuccess(int code, UpdateBean data);

    void betaUpdateFailed(int code, String msg);

    void getConfigSuccess(ConfigBean configBean);

    void getConfigFailed(int code, String msg);

    void newThemeFounded();

    void getAdvertSuccess(int code, AdvertBean advertBean);

    void getAdvertFailed(int code, String msg);

    void getReadLaterArticleSuccess(ReadLaterModel readLaterModel);

    void getReadLaterArticleFailed();

}
