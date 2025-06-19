package per.goweii.wanandroid.utils.web.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScrollingView;

import per.goweii.wanandroid.utils.web.WebScrollableUtils;

/**
 * @author CuiZhen
 * @date 2019/11/30
 * GitHub: https://github.com/goweii
 */
public class CustomWebView extends WebView implements ScrollingView {
    private Float touchX = null;
    private Float touchY = null;

    public CustomWebView(@NonNull Context context) {
        super(context);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
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
        super.scrollBy(x, y);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (super.canScrollHorizontally(direction)) {
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
        if (super.canScrollVertically(direction)) {
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

    @Override
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }
}
