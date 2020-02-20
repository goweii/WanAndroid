package per.goweii.wanandroid.module.mine.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.mine.model.CoinRankBean;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.view.CoinRankView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class CoinRankPresenter extends BasePresenter<CoinRankView> {

    public void getCoinRankList(int page) {
        addToRxLife(MineRequest.getCoinRankList(page, new RequestListener<CoinRankBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, CoinRankBean data) {
                if (isAttach()) {
                    getBaseView().getCoinRankListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getCoinRankListFail(code, msg);
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
