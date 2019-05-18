package per.goweii.wanandroid.module.home.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.home.model.HotKeyBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface SearchHistoryView extends BaseView {
    void getHotKeyListSuccess(int code, List<HotKeyBean> data);
    void getHotKeyListFail(int code, String msg);
}
