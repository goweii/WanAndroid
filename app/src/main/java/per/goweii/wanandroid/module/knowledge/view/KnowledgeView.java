package per.goweii.wanandroid.module.knowledge.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ChapterBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface KnowledgeView extends BaseView {
    void getKnowledgeListSuccess(int code, List<ChapterBean> data);
    void getKnowledgeListFail(int code, String msg);
}
