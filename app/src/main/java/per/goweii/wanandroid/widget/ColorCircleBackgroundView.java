package per.goweii.wanandroid.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import per.goweii.wanandroid.R;

public class ColorCircleBackgroundView extends View {
    private int color1 = Color.TRANSPARENT;
    private int color2 = Color.TRANSPARENT;
    private int color3 = Color.TRANSPARENT;
    private int color4 = Color.TRANSPARENT;
    private int color5 = Color.TRANSPARENT;

    public ColorCircleBackgroundView(Context context) {
        this(context, null);
    }

    public ColorCircleBackgroundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorCircleBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ColorCircleBackgroundView);
        color1 = typedArray.getColor(R.styleable.ColorCircleBackgroundView_ccbv_color1, color1);
        color2 = typedArray.getColor(R.styleable.ColorCircleBackgroundView_ccbv_color2, color2);
        color3 = typedArray.getColor(R.styleable.ColorCircleBackgroundView_ccbv_color3, color3);
        color4 = typedArray.getColor(R.styleable.ColorCircleBackgroundView_ccbv_color4, color4);
        color5 = typedArray.getColor(R.styleable.ColorCircleBackgroundView_ccbv_color5, color5);
        typedArray.recycle();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }
}
