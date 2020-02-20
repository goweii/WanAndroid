package per.goweii.wanandroid.module.mine.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.mine.model.CoinRecordBean;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.view.CoinView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class CoinPresenter extends BasePresenter<CoinView> {

    public void getCoin() {
        addToRxLife(MineRequest.getCoin(new RequestListener<Integer>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, Integer data) {
                if (isAttach()) {
                    getBaseView().getCoinSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getCoinFail(code, msg);
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

    public void getCoinRecordList(int page) {
        addToRxLife(MineRequest.getCoinRecordList(page, new RequestListener<CoinRecordBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, CoinRecordBean data) {
                if (isAttach()) {
                    getBaseView().getCoinRecordListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getCoinRecordListFail(code, msg);
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
