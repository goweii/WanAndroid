package per.goweii.wanandroid.module.mine.presenter;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.model.UserInfoBean;
import per.goweii.wanandroid.module.mine.view.MineView;
import per.goweii.wanandroid.utils.CookieUtils;
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

    public void getMessageUnreadCount() {
        if (!UserUtils.getInstance().isLogin()) {
            if (isAttach()) {
                getBaseView().getMessageUnreadCountSuccess(0);
            }
            return;
        }
        addToRxLife(MineRequest.getMessageUnreadCount(new RequestListener<Integer>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, Integer data) {
                if (isAttach()) {
                    getBaseView().getMessageUnreadCountSuccess(data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getMessageUnreadCountSuccess(0);
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
                getBaseView().getMessageUnreadCountSuccess(0);
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                String url = "https://www.wanandroid.com/";
                List<Cookie> cookies = CookieUtils.INSTANCE.loadForUrl(url);
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
                String num = null;
                try {
                    num = document.getElementsByClass("header_inbox").get(0)
                            .getElementsByTag("a").get(0)
                            .getElementsByTag("i").get(0)
                            .ownText();
                } catch (Exception ignore) {
                }
                if (TextUtils.isEmpty(num)) {
                    try {
                        Pattern pattern = Pattern.compile("你有([0-9]*)条未读消息");
                        String text = document.getElementsByClass("lead_list").get(0)
                                .getElementsMatchingOwnText(pattern).get(0)
                                .ownText();
                        Matcher matcher = pattern.matcher(text);
                        if (matcher.find()) {
                            num = matcher.group(1);
                        }
                    } catch (Exception ignore) {
                    }
                }
                int count = 0;
                if (!TextUtils.isEmpty(num)) {
                    count = Integer.parseInt(num);
                }
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
                    getBaseView().getMessageUnreadCountSuccess(integer);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (isAttach()) {
                    getBaseView().getMessageUnreadCountSuccess(0);
                }
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
