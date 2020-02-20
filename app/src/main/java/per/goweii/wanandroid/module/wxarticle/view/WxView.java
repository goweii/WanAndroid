package per.goweii.wanandroid.module.wxarticle.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.ChapterBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public interface WxView extends BaseView {
    void getWxArticleChaptersSuccess(int code, List<ChapterBean> data);
    void getWxArticleChaptersFailed(int code, String msg);
}
