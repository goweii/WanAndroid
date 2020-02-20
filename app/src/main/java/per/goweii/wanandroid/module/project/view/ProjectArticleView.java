package per.goweii.wanandroid.module.project.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface ProjectArticleView extends BaseView {
    void getProjectArticleListSuccess(int code, ArticleListBean data);
    void getProjectArticleListFailed(int code, String msg);
}
