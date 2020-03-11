package per.goweii.wanandroid.widget;

import android.content.Context;
import android.support.v4.view.ScrollingView;
import android.util.AttributeSet;

import com.daimajia.swipe.SwipeLayout;

/**
 * @author CuiZhen
 * @date 2019/9/7
 * GitHub: https://github.com/goweii
 */
public class NestedSwipeLayout extends SwipeLayout implements ScrollingView {

    public NestedSwipeLayout(Context context) {
        super(context);
    }

    public NestedSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedSwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        Status state = getOpenStatus();
        DragEdge edge = getDragEdge();
        switch (edge) {
            case Top:
                if (direction < 0) {
                    if (state == Status.Close) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (direction > 0) {
                    if (state == Status.Open) {
                        return false;
                    } else {
                        return true;
                    }
                }
                break;
            case Bottom:
                if (direction < 0) {
                    if (state == Status.Open) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (direction > 0) {
                    if (state == Status.Close) {
                        return false;
                    } else {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        Status state = getOpenStatus();
        DragEdge edge = getDragEdge();
        switch (edge) {
            case Left:
                if (direction < 0) {
                    if (state == Status.Close) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (direction > 0) {
                    if (state == Status.Open) {
                        return false;
                    } else {
                        return true;
                    }
                }
                break;
            case Right:
                if (direction < 0) {
                    if (state == Status.Open) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (direction > 0) {
                    if (state == Status.Close) {
                        return false;
                    } else {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return getDragDistance() + computeHorizontalScrollExtent();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        Status state = getOpenStatus();
        DragEdge edge = getDragEdge();
        switch (edge) {
            case Left:
                if (state == Status.Close) {
                    return 0;
                } else {
                    return -getDragDistance();
                }
            case Right:
                if (state == Status.Close) {
                    return 0;
                } else {
                    return getDragDistance();
                }
        }
        return 0;
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return getWidth();
    }

    @Override
    public int computeVerticalScrollRange() {
        return getDragDistance() + computeVerticalScrollExtent();
    }

    @Override
    public int computeVerticalScrollOffset() {
        Status state = getOpenStatus();
        DragEdge edge = getDragEdge();
        switch (edge) {
            case Top:
                if (state == Status.Close) {
                    return 0;
                } else {
                    return -getDragDistance();
                }
            case Bottom:
                if (state == Status.Close) {
                    return 0;
                } else {
                    return getDragDistance();
                }
        }
        return 0;
    }

    @Override
    public int computeVerticalScrollExtent() {
        return getHeight();
    }
}
