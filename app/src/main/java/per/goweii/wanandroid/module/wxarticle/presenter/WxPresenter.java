package per.goweii.wanandroid.module.wxarticle.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.ChapterBean;
import per.goweii.wanandroid.module.wxarticle.model.WxRequest;
import per.goweii.wanandroid.module.wxarticle.view.WxView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class WxPresenter extends BasePresenter<WxView> {

    public void getWxArticleChapters(){
        WxRequest.getWxArticleChapters(getRxLife(), new RequestListener<List<ChapterBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ChapterBean> data) {
                if (isAttach()) {
                    getBaseView().getWxArticleChaptersSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getWxArticleChaptersFailed(code, msg);
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

}
