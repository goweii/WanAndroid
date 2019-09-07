package per.goweii.wanandroid.module.main.view;

import android.graphics.PointF;

import per.goweii.basic.core.base.BaseView;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public interface WebView extends BaseView {
    void collectSuccess(PointF p);
    void collectFailed(String msg);
}
