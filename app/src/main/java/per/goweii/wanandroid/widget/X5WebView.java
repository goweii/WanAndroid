package per.goweii.wanandroid.widget;

import android.content.Context;
import android.support.v4.view.ScrollingView;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebView;

import java.util.Map;

/**
 * @author CuiZhen
 * @date 2019/11/30
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class X5WebView extends WebView implements ScrollingView {

    public X5WebView(Context context) {
        super(context);
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean b) {
        super(context, attributeSet, i, map, b);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return getView().canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return getView().canScrollVertically(direction);
    }
}
