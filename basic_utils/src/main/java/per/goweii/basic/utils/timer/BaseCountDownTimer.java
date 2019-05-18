package per.goweii.basic.utils.timer;

import android.os.CountDownTimer;

/**
 * 描述：毫秒倒计时
 *
 * @author Cuizhen
 * @date 2018/9/20
 */
public abstract class BaseCountDownTimer extends CountDownTimer {

    private boolean mStart = false;

    public BaseCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture + 499, countDownInterval);
    }

    @Override
    public final void onTick(long millisUntilFinished) {
        if (!mStart) {
            mStart = true;
            onTimerStart(millisUntilFinished);
        }
        onTimerTick(millisUntilFinished);
    }

    public boolean isStart() {
        return mStart;
    }

    public void finish(){
        onFinish();
        cancel();
    }

    @Override
    public final void onFinish() {
        onTimerFinish();
    }

    protected abstract void onTimerStart(long millisUntilFinished);

    protected abstract void onTimerTick(long millisUntilFinished);

    protected abstract void onTimerFinish();
}

