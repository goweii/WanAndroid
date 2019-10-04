package per.goweii.wanandroid.module.wxarticle.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface WxArticleView extends BaseView {
    void getWxArticleListSuccess(int code, ArticleListBean data);
    void getWxArticleListFailed(int code, String msg);

    void getWxArticleListSearchSuccess(int code, ArticleListBean data);
    void getWxArticleListSearchFailed(int code, String msg);
}
