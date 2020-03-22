package per.goweii.wanandroid.module.knowledge.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeRequest;
import per.goweii.wanandroid.module.knowledge.view.KnowledgeView;
import per.goweii.wanandroid.module.main.model.ChapterBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class KnowledgePresenter extends BasePresenter<KnowledgeView> {

    public void getKnowledgeListCache() {
        KnowledgeRequest.getKnowledgeListCache(new RequestListener<List<ChapterBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ChapterBean> data) {
                if (isAttach()) {
                    getBaseView().getKnowledgeListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    public void getKnowledgeList() {
        KnowledgeRequest.getKnowledgeList(getRxLife(), new RequestListener<List<ChapterBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ChapterBean> data) {
                if (isAttach()) {
                    getBaseView().getKnowledgeListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getKnowledgeListFail(code, msg);
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

    public void getKnowledgeListCacheAndNet() {
        KnowledgeRequest.getKnowledgeListCacheAndNet(getRxLife(), new RequestListener<List<ChapterBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ChapterBean> data) {
                if (isAttach()) {
                    getBaseView().getKnowledgeListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getKnowledgeListFail(code, msg);
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
