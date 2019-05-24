package per.goweii.wanandroid.module.project.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.project.model.ProjectChapterBean;
import per.goweii.wanandroid.module.project.model.ProjectRequest;
import per.goweii.wanandroid.module.project.view.ProjectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ProjectPresenter extends BasePresenter<ProjectView> {

    public void getProjectChapters(){
        ProjectRequest.getProjectChapters(getRxLife(), new RequestListener<List<ProjectChapterBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, List<ProjectChapterBean> data) {
                if (isAttachView()) {
                    getBaseView().getProjectChaptersSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttachView()) {
                    getBaseView().getProjectChaptersFailed(code, msg);
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
