package per.goweii.wanandroid.utils.web.view;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScrollingView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import per.goweii.wanandroid.utils.web.WebScrollableUtils;

/**
 * @author CuiZhen
 * @date 2019/11/30
 * GitHub: https://github.com/goweii
 */
public class ResuableWebView extends WebView implements ScrollingView {
    private final List<OnScrollChangeListener> mOnScrollChangeListeners = new LinkedList<>();
    private Float touchX = null;
    private Float touchY = null;

    public ResuableWebView(@NonNull Context context) {
        super(new MutableContextWrapper(context));
    }

    public ResuableWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(new MutableContextWrapper(context), attrs);
    }

    public ResuableWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(new MutableContextWrapper(context), attrs, defStyleAttr);
    }

    public ResuableWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(new MutableContextWrapper(context), attrs, defStyleAttr, defStyleRes);
    }

    public ResuableWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(new MutableContextWrapper(context), attrs, defStyleAttr, privateBrowsing);
    }

    public void setBaseContext(@NonNull Context context) {
        final MutableContextWrapper contextWrapper = (MutableContextWrapper) this.getContext();
        contextWrapper.setBaseContext(context);
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (int i = 0; i < mOnScrollChangeListeners.size(); i++) {
            OnScrollChangeListener listener = mOnScrollChangeListeners.get(i);
            if (listener != null) {
                listener.onScrollChange(this, l, t, oldl, oldt);
            }
        }
    }

    public void addOnScrollChangeListener(OnScrollChangeListener l) {
        if (l != null) {
            mOnScrollChangeListeners.add(l);
        }
    }

    public void removeOnScrollChangeListener(OnScrollChangeListener l) {
        if (l != null) {
            mOnScrollChangeListeners.remove(l);
        }
    }

    public void clearOnScrollChangeListeners() {
        mOnScrollChangeListeners.clear();
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

    public interface OnScrollChangeListener {
        void onScrollChange(@NonNull ResuableWebView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}
