package per.goweii.wanandroid.module.navigation.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.navigation.model.NaviBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface NaviView extends BaseView {
    void getNaviListSuccess(int code, List<NaviBean> data);
    void getNaviListFail(int code, String msg);
}
