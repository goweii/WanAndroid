package per.goweii.wanandroid.module.mine.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.mine.model.CollectionArticleBean;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface CollectionArticleView extends BaseView {
    void getCollectArticleListSuccess(int code, CollectionArticleBean data);
    void getCollectArticleListFailed(int code, String msg);
}
