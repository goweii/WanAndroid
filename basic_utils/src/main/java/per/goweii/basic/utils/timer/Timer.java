package per.goweii.basic.utils.timer;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.IntRange;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/3/25
 */
public class Timer implements Runnable {

    private static final int STATE_IDLE = -1;
    private static final int STATE_RUNNING = 0;
    private static final int STATE_PAUSE = 1;
    private static final int STATE_FINISHING = 2;

    private static final int WHAT_START = 0;
    private static final int WHAT_TICK = 1;
    private static final int WHAT_FINISH = 2;
    private static final int WHAT_PAUSE = 3;
    private static final int WHAT_RESUME = 4;

    private int mState = STATE_IDLE;

    private boolean mIncrease;
    private long mInitial = 0;
    private long mDelay = 1000;
    private long mTime = 0;
    private ScheduledExecutorService mService;
    private TimerListener mListener = null;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (mListener == null) {
                return;
            }
            switch (msg.what) {
                default:
                    break;
                case WHAT_START:
                    mListener.onStart(mTime);
                    break;
                case WHAT_TICK:
                    mListener.onTick(mTime);
                    break;
                case WHAT_FINISH:
                    mListener.onFinish(mTime);
                    break;
                case WHAT_PAUSE:
                    mListener.onPause(mTime);
                    break;
                case WHAT_RESUME:
                    mListener.onResume(mTime);
                    break;
            }
        }
    };

    private Timer() {
    }

    public static Timer create() {
        return new Timer();
    }

    public Timer setIncrease(boolean increase) {
        mIncrease = increase;
        return this;
    }

    public Timer initial(@IntRange(from = 0) long milliseconds) {
        mInitial = milliseconds;
        return this;
    }

    public Timer delay(@IntRange(from = 0) long milliseconds) {
        mDelay = milliseconds;
        return this;
    }

    public Timer listener(TimerListener listener) {
        mListener = listener;
        return this;
    }

    public void start() {
        mState = STATE_RUNNING;
        if (mService == null || mService.isShutdown()) {
            mService = Executors.newScheduledThreadPool(1);
        }
        mService.scheduleWithFixedDelay(this, mDelay, mDelay, TimeUnit.MILLISECONDS);
        mTime = mInitial;
        mHandler.sendEmptyMessage(WHAT_START);
    }

    public void pause() {
        if (mState == STATE_RUNNING) {
            mState = STATE_PAUSE;
            mHandler.sendEmptyMessage(WHAT_PAUSE);
        }
    }

    public void resume() {
        if (mState == STATE_PAUSE) {
            mState = STATE_RUNNING;
            mHandler.sendEmptyMessage(WHAT_RESUME);
        }
    }

    public void finish() {
        mState = STATE_FINISHING;
        if (mService != null && !mService.isShutdown()) {
            mService.shutdown();
            mService = null;
        }
    }

    public void finishNow() {
        mState = STATE_IDLE;
        if (mService != null && !mService.isShutdown()) {
            mService.shutdownNow();
            mService = null;
        }
        mHandler.sendEmptyMessage(WHAT_FINISH);
    }

    @Override
    public void run() {
        switch (mState) {
            default:
                break;
            case STATE_IDLE:
                break;
            case STATE_RUNNING:
                increaseTime();
                if (mTime > 0) {
                    mHandler.sendEmptyMessage(WHAT_TICK);
                } else {
                    finishNow();
                }
                break;
            case STATE_PAUSE:
                break;
            case STATE_FINISHING:
                increaseTime();
                mHandler.sendEmptyMessage(WHAT_FINISH);
                break;
        }
    }

    private void increaseTime(){
        if (mIncrease) {
            mTime += mDelay;
        } else {
            mTime -= mDelay;
        }
        if (mTime < 0){
            mTime = 0;
        }
    }

    public interface TimerListener {
        void onStart(long time);

        void onTick(long time);

        void onFinish(long time);

        void onPause(long time);

        void onResume(long time);
    }

}
