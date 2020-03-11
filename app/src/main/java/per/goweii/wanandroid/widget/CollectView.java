package per.goweii.wanandroid.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import per.goweii.heartview.HeartView;
import per.goweii.reveallayout.RevealLayout;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class CollectView extends RevealLayout implements View.OnTouchListener {

    private OnClickListener mOnClickListener = null;
    private int mUncheckedColor;

    public CollectView(Context context) {
        this(context, null);
    }

    public CollectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttr(AttributeSet attrs) {
        super.initAttr(attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CollectView);
        mUncheckedColor = typedArray.getColor(R.styleable.CollectView_cv_uncheckedColor, 0);
        typedArray.recycle();
        setCheckWithExpand(true);
        setUncheckWithExpand(false);
        setCheckedLayoutId(R.layout.layout_collect_view_checked);
        setUncheckedLayoutId(R.layout.layout_collect_view_unchecked);
        setAnimDuration(500);
        setAllowRevert(true);
        setOnTouchListener(this);
    }

    @Override
    protected View createUncheckedView() {
        View view = super.createUncheckedView();
        if (view instanceof HeartView) {
            HeartView heartView = (HeartView) view;
            if (mUncheckedColor != 0) {
                heartView.setColor(mUncheckedColor);
                heartView.setEdgeColor(mUncheckedColor);
            }
        }
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            default:
                break;
            case MotionEvent.ACTION_UP:
                if (UserUtils.getInstance().doIfLogin(v.getContext())) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(this);
                    }
                }
                break;
        }
        return !UserUtils.getInstance().isLogin();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(CollectView v);
    }
}