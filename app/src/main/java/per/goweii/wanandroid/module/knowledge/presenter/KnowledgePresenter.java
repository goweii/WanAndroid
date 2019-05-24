package per.goweii.wanandroid.module.knowledge.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeBean;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeRequest;
import per.goweii.wanandroid.module.knowledge.view.KnowledgeView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgePresenter extends BasePresenter<KnowledgeView> {

    public void getKnowledgeList() {
        KnowledgeRequest.getKnowledgeList(getRxLife(), new RequestListener<List<KnowledgeBean>>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, List<KnowledgeBean> data) {
                if (isAttachView()) {
                    getBaseView().getKnowledgeListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getKnowledgeListFail(code, msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        });
    }
}
