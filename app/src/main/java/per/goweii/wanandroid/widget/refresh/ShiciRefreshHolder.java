package per.goweii.wanandroid.widget.refresh;

import java.util.ArrayList;
import java.util.List;

import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.module.main.model.JinrishiciBean;
import per.goweii.wanandroid.module.main.model.MainRequest;

/**
 * @author CuiZhen
 * @date 2020/5/10
 */
public class ShiciRefreshHolder {

    private static ShiciRefreshHolder instance = null;
    private static final int MAX_SIZE = 3;

    public static ShiciRefreshHolder instance() {
        if (instance == null) {
            instance = new ShiciRefreshHolder();
        }
        return instance;
    }

    private final RxLife mRxLife;

    private final List<String> mCache = new ArrayList<>(MAX_SIZE);

    private ShiciRefreshHolder() {
        mRxLife = RxLife.create();
        request();
    }

    private void request() {
        if (mCache.size() >= MAX_SIZE) {
            return;
        }
        MainRequest.getJinrishici(mRxLife, new RequestCallback<JinrishiciBean>() {
            @Override
            public void onSuccess(int code, JinrishiciBean data) {
                mCache.add(data.getContent());
                request();
            }

            @Override
            public void onFailed(int code, String msg) {
            }
        });
    }

    public void refresh() {
        if (mCache.size() >= 2) {
            mCache.remove(0);
        }
        request();
    }

    public String get() {
        if (mCache.isEmpty()) {
            return null;
        }
        return mCache.get(0);
    }

}
