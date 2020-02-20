package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.mine.model.CoinRankBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface CoinRankView extends BaseView {
    void getCoinRankListSuccess(int code, CoinRankBean data);

    void getCoinRankListFail(int code, String msg);
}
