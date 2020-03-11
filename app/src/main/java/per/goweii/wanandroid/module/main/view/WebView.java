package per.goweii.wanandroid.module.main.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.CollectArticleEntity;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public interface WebView extends BaseView {
    void collectSuccess(CollectArticleEntity entity);
    void collectFailed(String msg);

    void uncollectSuccess(CollectArticleEntity entity);

    void uncollectFailed(String msg);
}
