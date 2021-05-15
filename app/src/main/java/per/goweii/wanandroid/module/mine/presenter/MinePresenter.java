package per.goweii.wanandroid.module.mine.presenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.model.UserInfoBean;
import per.goweii.wanandroid.module.mine.view.MineView;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class MinePresenter extends BasePresenter<MineView> {

    public UserInfoBean mUserInfoBean = null;

    public void getUserInfo() {
        addToRxLife(MineRequest.getUserInfo(new RequestListener<UserInfoBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, UserInfoBean data) {
                mUserInfoBean = data;
                if (isAttach()) {
                    getBaseView().getUserInfoSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getUserInfoFail(code, msg);
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

    public void getNotificationCount() {
        if (!UserUtils.getInstance().isLogin()) {
            if (isAttach()) {
                getBaseView().getNotificationCountSuccess(0);
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                String url = "https://www.wanandroid.com/";
                List<Cookie> cookies = WanApp.getCookieJar().loadForRequest(HttpUrl.get(url));
                if (cookies == null || cookies.isEmpty()) {
                    emitter.onNext(0);
                    emitter.onComplete();
                    return;
                }
                Map<String, String> map = new HashMap<>(cookies.size());
                for (Cookie cookie : cookies) {
                    map.put(cookie.name(), cookie.value());
                }
                Document document = Jsoup.connect("https://www.wanandroid.com/")
                        .cookies(map)
                        .get();
                Elements newMsgDotElements = document.getElementsByClass("newMsgDot");
                Element newMsgDotElement = newMsgDotElements.get(0);
                Elements aElements = newMsgDotElement.getElementsByTag("a");
                Element aElement = aElements.get(0);
                String num = aElement.ownText();
                int count = Integer.parseInt(num);
                emitter.onNext(count);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                addToRxLife(d);
            }

            @Override
            public void onNext(Integer integer) {
                if (isAttach()) {
                    getBaseView().getNotificationCountSuccess(integer);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (isAttach()) {
                    getBaseView().getNotificationCountSuccess(0);
                }
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
