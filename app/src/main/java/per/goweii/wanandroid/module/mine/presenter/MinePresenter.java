package per.goweii.wanandroid.module.mine.presenter;

import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.view.MineView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class MinePresenter extends BasePresenter<MineView> {

    public void getUserCoin() {
        addToRxLife(MineRequest.getCoin(new RequestListener<Integer>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, Integer data) {
                if (isAttachView()) {
                    getBaseView().getUserCoinSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getUserCoinFail(code, msg);
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

    public void getUserLevel() {
        Observable.just("https://www.wanandroid.com/index")
                .map(new Function<String, Call>() {
                    @Override
                    public Call apply(String url) throws Exception {
                        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .cookieJar(WanApp.getCookieJar())
                                .build();
                        final Request request = new Request.Builder()
                                .url(url)
                                .get()
                                .build();
                        return okHttpClient.newCall(request);
                    }
                })
                .map(new Function<Call, String>() {
                    @Override
                    public String apply(Call call) throws Exception {
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                return response.body().string();
                            }
                        }
                        return "";
                    }
                })
                .map(new Function<String, String[]>() {
                    @Override
                    public String[] apply(String s) throws Exception {
                        if (TextUtils.isEmpty(s)) {
                            return new String[0];
                        }
                        int index = s.indexOf("本站积分");
                        int coinIndexStart = s.indexOf(">", index);
                        int coinIndexEnd = s.indexOf("<", coinIndexStart);
                        String coinStr = s.substring(coinIndexStart + 1, coinIndexEnd).trim();
                        int lvIndexStart = s.indexOf(">", coinIndexEnd);
                        int lvIndexEnd = s.indexOf("<", lvIndexStart);
                        String lvStr = s.substring(lvIndexStart + 1, lvIndexEnd).replace("lv", "").trim();
                        int rankingIndexStart = s.indexOf("排名", lvIndexEnd);
                        int rankingIndexEnd = s.indexOf("<", rankingIndexStart);
                        String rankingStr = s.substring(rankingIndexStart + 2, rankingIndexEnd).replace(" ", "").trim();
                        Integer.parseInt(coinStr);
                        Integer.parseInt(lvStr);
                        Integer.parseInt(rankingStr);
                        return new String[]{coinStr, lvStr, rankingStr};
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addToRxLife(d);
                    }

                    @Override
                    public void onNext(String[] strings) {
                        if (strings.length == 3) {
                            getBaseView().getUserCoinAndLevelSuccess(strings[0], strings[1], strings[2]);
                        } else {
                            getBaseView().getUserCoinAndLevelFail();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getBaseView().getUserCoinAndLevelFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
