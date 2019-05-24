package per.goweii.wanandroid.module.project.presenter;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.project.model.ProjectArticleBean;
import per.goweii.wanandroid.module.project.model.ProjectRequest;
import per.goweii.wanandroid.module.project.view.ProjectArticleView;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ProjectArticlePresenter extends BasePresenter<ProjectArticleView> {

    public void getProjectArticleList(int id, int page){
        ProjectRequest.getProjectArticleList(getRxLife(), id, page, new RequestListener<ProjectArticleBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, ProjectArticleBean data) {
                if (isAttachView()) {
                    getBaseView().getProjectArticleListSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getProjectArticleListFailed(code, msg);
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

    public void collect(ProjectArticleBean.DatasBean item, final CollectView v){
        addToRxLife(MainRequest.collect(item.getId(), new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                item.setCollect(true);
                if (!v.isChecked()) {
                    v.toggle();
                }
                CollectionEvent.postCollectWithArticleId(item.getId());
            }

            @Override
            public void onFailed(int code, String msg) {
                if (v.isChecked()) {
                    v.toggle();
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

    public void uncollect(ProjectArticleBean.DatasBean item, final CollectView v){
        addToRxLife(MainRequest.uncollect(item.getId(), new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                item.setCollect(false);
                if (v.isChecked()) {
                    v.toggle();
                }
                CollectionEvent.postUnCollectWithArticleId(item.getId());
            }

            @Override
            public void onFailed(int code, String msg) {
                if (!v.isChecked()) {
                    v.toggle();
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
