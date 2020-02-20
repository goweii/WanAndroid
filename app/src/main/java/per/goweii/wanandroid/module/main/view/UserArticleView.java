package per.goweii.wanandroid.module.main.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/10/3
 * GitHub: https://github.com/goweii
 */
public interface UserArticleView extends BaseView {
    void getUserArticleListSuccess(int code, ArticleListBean data);

    void getUserArticleListFailed(int code, String msg);
}
