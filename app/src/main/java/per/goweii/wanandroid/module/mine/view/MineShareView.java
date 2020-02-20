package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public interface MineShareView extends BaseView {
    void getMineShareArticleListSuccess(int code, ArticleListBean data);

    void getMineShareArticleListFailed(int code, String msg);
}
