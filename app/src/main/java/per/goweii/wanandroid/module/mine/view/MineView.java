package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface MineView extends BaseView {
    void getUserCoinAndLevelSuccess(String coin, String lv);

    void getUserCoinAndLevelFail();
}
