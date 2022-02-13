package per.goweii.wanandroid.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class ListenableNestedScrollView extends NestedScrollView {
    private OnScrollChanged mOnScrollChanged = null;

    public ListenableNestedScrollView(Context context) {
        this(context, null);
    }

    public ListenableNestedScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListenableNestedScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChanged != null) {
            float curScroll = 0F;
            float maxScroll = 0F;
            if (getChildCount() > 0) {
                curScroll = getScrollY();
                curScroll = Math.max(0F, curScroll);
                float meHeight = getHeight() - getPaddingTop() - getPaddingBottom();
                maxScroll = getChildAt(0).getHeight() - meHeight;
                maxScroll = Math.max(0F, maxScroll);
            }
            mOnScrollChanged.onScrollChanged(curScroll, maxScroll);
        }
    }

    public void setOnScrollChanged(@Nullable OnScrollChanged onScrollChanged) {
        mOnScrollChanged = onScrollChanged;
    }

    public interface OnScrollChanged {
        void onScrollChanged(float curScroll, float maxScroll);
    }
}
