package per.goweii.wanandroid.module.home.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface SearchResultView extends BaseView {
    void searchSuccess(int code, ArticleListBean data);
    void searchFailed(int code, String msg);
}
