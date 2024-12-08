package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public interface SettingView extends BaseView {
    void logoutSuccess(int code, BaseBean data);

    void logoutFailed(int code, String msg);

    void getCacheSizeSuccess(String size);
}
