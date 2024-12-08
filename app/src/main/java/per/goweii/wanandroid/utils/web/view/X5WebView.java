package per.goweii.wanandroid.utils.web.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.ScrollingView;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import per.goweii.basic.utils.LogUtils;
import per.goweii.wanandroid.utils.web.WebScrollableUtils;

/**
 * @author CuiZhen
 * @date 2019/11/30
 * GitHub: https://github.com/goweii
 */
public class X5WebView extends WebView implements ScrollingView {
    private Float touchX = null;
    private Float touchY = null;

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchX = ev.getX();
                touchY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchX = null;
                touchY = null;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void scrollBy(int x, int y) {
        getView().scrollBy(x, y);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (getView().canScrollHorizontally(direction)) {
            return true;
        }
        if (touchX == null || touchY == null) {
            return false;
        }
        int directions = WebScrollableUtils.INSTANCE.getScrollableDirections(this, touchX, touchY);
        if (direction < 0 && (directions & WebScrollableUtils.DIRECTION_LEFT) != 0) {
            return true;
        }
        if (direction > 0 && (directions & WebScrollableUtils.DIRECTION_RIGHT) != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (getView().canScrollVertically(direction)) {
            return true;
        }
        if (touchX == null || touchY == null) {
            return false;
        }
        int directions = WebScrollableUtils.INSTANCE.getScrollableDirections(this, touchX, touchY);
        if (direction < 0 && (directions & WebScrollableUtils.DIRECTION_UP) != 0) {
            return true;
        }
        if (direction > 0 && (directions & WebScrollableUtils.DIRECTION_DOWN) != 0) {
            return true;
        }
        return false;
    }
}
