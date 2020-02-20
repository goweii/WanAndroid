package per.goweii.wanandroid.module.home.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.home.model.HomeRequest;
import per.goweii.wanandroid.module.home.model.HotKeyBean;
import per.goweii.wanandroid.module.home.view.SearchHistoryView;
import per.goweii.wanandroid.utils.SearchHistoryUtils;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class SearchHistoryPresenter extends BasePresenter<SearchHistoryView> {

    private final SearchHistoryUtils mSearchHistoryUtils = SearchHistoryUtils.newInstance();

    public void getHotKeyList(){
        HomeRequest.getHotKeyList(getRxLife(), new RequestListener<List<HotKeyBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<HotKeyBean> data) {
                if (isAttach()) {
                    getBaseView().getHotKeyListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getHotKeyListFail(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    public List<String> getHistory(){
        return mSearchHistoryUtils.get();
    }

    public void saveHistory(List<String> list){
        List<String> saves = list;
        int max = SettingUtils.getInstance().getSearchHistoryMaxCount();
        if (list != null && list.size() > max) {
            saves = list.subList(0, max - 1);
        }
        mSearchHistoryUtils.save(saves);
    }

}
