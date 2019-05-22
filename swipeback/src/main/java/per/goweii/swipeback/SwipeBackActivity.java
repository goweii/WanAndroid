package per.goweii.swipeback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SwipeBackActivity extends AppCompatActivity {

    protected SwipeBack mSwipeBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBack = SwipeBack.inject(this);
        mSwipeBack.setTakeOverActivityEnterExitAnim(supportTakeOverActivityEnterExitAnim());
        mSwipeBack.setSwipeBackEnable(supportSwipeBack());
        mSwipeBack.setForceEdgeEnable(supportForceEdge());
        mSwipeBack.setSwipeDirection(supportSwipeDirection());
        mSwipeBack.getSwipeBackLayout().setShadowStartColor(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBack.onPostCreate();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        mSwipeBack.onEnterAnimationComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSwipeBack.onDestroy();
    }

    @Override
    public void finish() {
        if (mSwipeBack.finish()) {
            super.finish();
        }
    }

    protected boolean supportSwipeBack() {
        return true;
    }

    protected boolean supportForceEdge() {
        return true;
    }

    protected boolean supportTakeOverActivityEnterExitAnim() {
        return true;
    }

    @SwipeDirection
    protected int supportSwipeDirection() {
        return SwipeDirection.FROM_LEFT;
    }
}
