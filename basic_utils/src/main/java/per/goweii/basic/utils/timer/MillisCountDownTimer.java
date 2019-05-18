package per.goweii.basic.utils.timer;

/**
 * 毫秒倒计时
 *
 * @author Cuizhen
 * @date 2018/9/20
 */
public class MillisCountDownTimer extends BaseCountDownTimer {

    private OnTimerStartListener mOnTimerStartListener = null;
    private OnTimerTickListener mOnTimerTickListener = null;
    private OnTimerFinishListener mOnTimerFinishListener = null;

    public MillisCountDownTimer(long duration, long delay) {
        super(duration, delay);
    }

    @Override
    protected void onTimerStart(long millisUntilFinished) {
        if (mOnTimerStartListener != null) {
            mOnTimerStartListener.onStart(millisUntilFinished);
        }
    }

    @Override
    protected void onTimerTick(long millisUntilFinished) {
        if (mOnTimerTickListener != null) {
            mOnTimerTickListener.onTick(millisUntilFinished);
        }
    }

    @Override
    protected void onTimerFinish() {
        if (mOnTimerFinishListener != null) {
            mOnTimerFinishListener.onFinish();
        }
    }

    public void setOnTimerStartListener(OnTimerStartListener onTimerStartListener) {
        mOnTimerStartListener = onTimerStartListener;
    }

    public void setOnTimerTickListener(OnTimerTickListener onTimerTickListener) {
        mOnTimerTickListener = onTimerTickListener;
    }

    public void setOnTimerFinishListener(OnTimerFinishListener onTimerFinishListener) {
        mOnTimerFinishListener = onTimerFinishListener;
    }

    public interface OnTimerStartListener {
        void onStart(long millisUntilFinished);
    }

    public interface OnTimerTickListener {
        void onTick(long millisUntilFinished);
    }

    public interface OnTimerFinishListener {
        void onFinish();
    }
}

