package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public interface CollectionArticleView extends BaseView {
    void getCollectArticleListSuccess(int code, ArticleListBean data);
    void getCollectArticleListFailed(int code, String msg);
}
