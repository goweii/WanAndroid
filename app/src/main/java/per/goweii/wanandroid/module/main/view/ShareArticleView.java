package per.goweii.wanandroid.module.main.view;

import per.goweii.basic.core.base.BaseView;
import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/10/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface ShareArticleView extends BaseView {
    void shareArticleSuccess(int code, BaseBean data);

    void shareArticleFailed(int code, String msg);
}
