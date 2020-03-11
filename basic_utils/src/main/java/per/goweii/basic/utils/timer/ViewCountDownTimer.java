package per.goweii.basic.utils.timer;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 描述：对一个View进行倒计时
 *
 * @author Cuizhen
 * @date 2018/9/20
 */
public class ViewCountDownTimer<V extends View> implements SecondCountDownTimer.OnTimerTickListener, SecondCountDownTimer.OnTimerStartListener, SecondCountDownTimer.OnTimerFinishListener {

    private final SecondCountDownTimer mSecondTimer;
    private final V mTimerView;
    private OnTimerListener<V> mOnTimerListener = null;

    public ViewCountDownTimer(int second, @NonNull V timerView) {
        this.mTimerView = timerView;
        mSecondTimer = new SecondCountDownTimer(second, 1);
        mSecondTimer.setOnTimerStartListener(this);
        mSecondTimer.setOnTimerTickListener(this);
        mSecondTimer.setOnTimerFinishListener(this);
    }

    public ViewCountDownTimer<V> setOnTimerListener(OnTimerListener<V> onTimerListener) {
        this.mOnTimerListener = onTimerListener;
        return this;
    }

    public void start(){
        if (!mSecondTimer.isStart()) {
            mSecondTimer.start();
        }
    }

    public void cancel(){
        mSecondTimer.cancel();
    }

    @Override
    public void onTick(long secondUntilFinished) {
        if (mOnTimerListener != null) {
            mOnTimerListener.onTick(mTimerView, secondUntilFinished);
        }
    }

    @Override
    public void onStart(long secondUntilFinished) {
        if (mOnTimerListener != null) {
            mOnTimerListener.onStart(mTimerView, secondUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        if (mOnTimerListener != null) {
            mOnTimerListener.onFinish(mTimerView);
        }
    }

    public interface OnTimerListener<V> {
        void onStart(V timerView, long secondUntilFinished);
        void onTick(V timerView, long secondUntilFinished);
        void onFinish(V timerView);
    }
}
