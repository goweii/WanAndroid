package per.goweii.wanandroid.widget.refresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.classics.ArrowDrawable;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;

import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.wanandroid.R;

public class ShiciRefreshHeader extends SimpleComponent implements RefreshHeader {
    public static String REFRESH_HEADER_PULLING = "下拉刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新";
    public static String REFRESH_HEADER_RELEASE = "释放刷新";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";
    private final ImageView imageView;
    private final TextView textView;
    private int color;
    private final ArrowDrawable arrowDrawable;
    private final ProgressDrawable progressDrawable;
    private final Drawable successDrawable;
    private final Drawable failureDrawable;

    static {
        ShiciRefreshHolder.instance().refresh();
    }

    private String textPulling() {
        String shici = ShiciRefreshHolder.instance().get();
        if (!TextUtils.isEmpty(shici)) return shici;
        return REFRESH_HEADER_PULLING;
    }

    private String textRefreshing() {
        String shici = ShiciRefreshHolder.instance().get();
        if (!TextUtils.isEmpty(shici)) return shici;
        return REFRESH_HEADER_REFRESHING;
    }

    private String textRelease() {
        String shici = ShiciRefreshHolder.instance().get();
        if (!TextUtils.isEmpty(shici)) return shici;
        return REFRESH_HEADER_RELEASE;
    }

    private String textFinish() {
        String shici = ShiciRefreshHolder.instance().get();
        if (!TextUtils.isEmpty(shici)) return shici;
        return REFRESH_HEADER_FINISH;
    }

    private String textFailed() {
        String shici = ShiciRefreshHolder.instance().get();
        if (!TextUtils.isEmpty(shici)) return shici;
        return REFRESH_HEADER_FAILED;
    }

    public ShiciRefreshHeader(Context context) {
        this(context, null);
    }

    public ShiciRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        int padding = (int) DisplayInfoUtils.getInstance().dp2px(20);
        setPadding(0, padding, 0, padding);
        View.inflate(context, R.layout.layout_shici_refresh_header, this);
        color = ResUtils.getThemeColor(context, R.attr.colorTextSecond);
        imageView = findViewById(R.id.iv_shici_refresh_header_state);
        textView = findViewById(R.id.tv_shici_refresh_header_content);
        imageView.animate().setInterpolator(null);
        textView.setTextColor(color);
        arrowDrawable = new ArrowDrawable();
        arrowDrawable.setColor(color);
        progressDrawable = new ProgressDrawable();
        progressDrawable.setColor(color);
        successDrawable = ResUtils.getDrawable(R.drawable.ic_success);
        successDrawable.setTint(color);
        failureDrawable = ResUtils.getDrawable(R.drawable.ic_failure);
        failureDrawable.setTint(color);
        setColor(color);
        setAlpha(0.4F);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        imageView.animate().cancel();
        progressDrawable.stop();
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        textView.setTextColor(color);
        imageView.setColorFilter(color);
        arrowDrawable.setColor(color);
        progressDrawable.setColor(color);
        successDrawable.setTint(color);
        failureDrawable.setTint(color);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (success) {
            imageView.setImageDrawable(successDrawable);
            textView.setText(textFinish());
        } else {
            imageView.setImageDrawable(failureDrawable);
            textView.setText(textFailed());
        }
        ShiciRefreshHolder.instance().refresh();
        return 500;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        super.onStateChanged(refreshLayout, oldState, newState);
        switch (newState) {
            case None:
                imageView.animate().cancel();
                imageView.setRotation(0F);
                progressDrawable.stop();
                imageView.setImageResource(android.R.color.transparent);
                textView.setText("");
                break;
            case PullDownToRefresh:
                textView.setText(textPulling());
                imageView.animate().rotation(0);
                imageView.setImageDrawable(arrowDrawable);
                break;
            case ReleaseToRefresh:
                textView.setText(textRelease());
                imageView.animate().rotation(180);
                imageView.setImageDrawable(arrowDrawable);
                break;
            case RefreshReleased:
                textView.setText(textRefreshing());
                imageView.animate().cancel();
                imageView.setRotation(0F);
                imageView.setImageDrawable(progressDrawable);
                progressDrawable.start();
                break;
            case Refreshing:
                break;
            case RefreshFinish:
                imageView.animate().cancel();
                imageView.setRotation(0F);
                progressDrawable.stop();
                break;
        }
    }

}
