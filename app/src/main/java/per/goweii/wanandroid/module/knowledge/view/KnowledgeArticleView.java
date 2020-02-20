package per.goweii.wanandroid.module.knowledge.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ArticleListBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface KnowledgeArticleView extends BaseView {
    void getKnowledgeArticleListSuccess(int code, ArticleListBean data);
    void getKnowledgeArticleListFail(int code, String msg);
}
