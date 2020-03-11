package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.mine.model.UserInfoBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface MineView extends BaseView {
    void getUserInfoSuccess(int code, UserInfoBean coin);

    void getUserInfoFail(int code, String msg);
}
