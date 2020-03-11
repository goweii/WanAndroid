package per.goweii.wanandroid.module.home.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface HomeView extends BaseView {
    void allFail();
    void getBannerSuccess(int code, List<BannerBean> data);
    void getBannerFail(int code, String msg);

    void getArticleListSuccess(int code, ArticleListBean data);
    void getArticleListFailed(int code, String msg);
    void getTopArticleListSuccess(int code, List<ArticleBean> data);
    void getTopArticleListFailed(int code, String msg);
}
