package per.goweii.wanandroid.module.wxarticle.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.wxarticle.model.WxChapterBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface WxView extends BaseView {
    void getWxArticleChaptersSuccess(int code, List<WxChapterBean> data);
    void getWxArticleChaptersFailed(int code, String msg);
}
