package per.goweii.basic.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.basic.ui.R;
import per.goweii.statusbarcompat.StatusBarCompat;

/**
 * @author CuiZhen
 * @date 2019/8/25
 * GitHub: https://github.com/goweii
 */
public class StatusBarView extends View {
    private IconMode iconMode;
    private Transparent transparent;

    public StatusBarView(Context context) {
        this(context, null);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusBarView);
        int iconModeInt = typedArray.getInt(R.styleable.StatusBarView_sbv_iconMode, 0);
        int transparentInt = typedArray.getInt(R.styleable.StatusBarView_sbv_transparent, 0);
        iconMode = IconMode.values()[iconModeInt];
        transparent = Transparent.values()[transparentInt];
        typedArray.recycle();
        refreshIconMode();
        refreshTransparent();
    }

    public void setIconMode(@NonNull IconMode iconMode) {
        this.iconMode = iconMode;
        refreshIconMode();
    }

    public void setTransparent(@NonNull Transparent transparent) {
        this.transparent = transparent;
        refreshTransparent();
    }

    public void refreshIconMode() {
        switch (iconMode) {
            case UNCHANGED:
                break;
            case DARK_ICON:
                StatusBarCompat.setIconDark(getContext());
                break;
            case LIGHT_ICON:
                StatusBarCompat.setIconLight(getContext());
                break;
        }
    }

    public void refreshTransparent() {
        switch (transparent) {
            case UNCHANGED:
                break;
            case TRANSPARENT:
                StatusBarCompat.transparent(getContext());
                break;
            case UNTRANSPARENT:
                StatusBarCompat.unTransparent(getContext());
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                getStatusBarHeight() + getPaddingTop() + getPaddingBottom());
    }

    public int getStatusBarHeight() {
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public enum Transparent {
        UNCHANGED,
        TRANSPARENT,
        UNTRANSPARENT,
        ;
    }

    public enum IconMode {
        UNCHANGED,
        DARK_ICON,
        LIGHT_ICON,
        ;
    }
}
