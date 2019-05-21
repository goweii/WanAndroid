package per.goweii.swipeback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SwipeBackActivity extends AppCompatActivity {

    private SwipeBack mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = SwipeBack.inject(this);
        mHelper.onCreate();
        mHelper.setFinishAnimEnable(supportFinishAnim());
        mHelper.setSwipeBackEnable(supportSwipeBack());
        mHelper.setForceEdgeEnable(supportForceEdge());
        mHelper.setSwipeDirection(supportSwipeDirection());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        mHelper.onEnterAnimationComplete();
    }

    @Override
    public void finish() {
        super.finish();
        mHelper.finish();
    }

    public boolean supportSwipeBack() {
        return true;
    }

    public boolean supportForceEdge() {
        return true;
    }

    public boolean supportFinishAnim() {
        return true;
    }

    @SwipeDirection
    public int supportSwipeDirection() {
        return SwipeDirection.FROM_LEFT;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }
}
