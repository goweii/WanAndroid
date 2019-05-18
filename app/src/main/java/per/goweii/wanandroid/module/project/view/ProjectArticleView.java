package per.goweii.wanandroid.module.project.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.project.model.ProjectArticleBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface ProjectArticleView extends BaseView {
    void getProjectArticleListSuccess(int code, ProjectArticleBean data);
    void getProjectArticleListFailed(int code, String msg);
}
