package per.goweii.basic.ui.toast;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.StringRes;

import java.lang.reflect.Field;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.ui.R;
import per.goweii.basic.utils.Utils;

/**
 * Toast的工具类
 * 防止重复点击队列形式出现长时间不消失
 * 链式调用，方便定制
 *
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/10-下午2:09
 */
@SuppressLint("StaticFieldLeak")
public class ToastMaker {
    private static final int MAKE = 0;
    private static final int SHOW = 1;
    private static final int DURATION = 2;
    private static final int GRAVITY = 3;
    private static final int VIEW = 4;
    private static final int MARGIN = 5;
    private static final int ANIMATION = 6;
    private static final int TEXT = 7;
    private static Context context = null;
    private static Toast toast = null;
    private static Handler sHandler = null;

    static {
        Utils.onInit(new Utils.OnInit() {
            @Override
            public void onInit(Context context) {
                init(context);
            }
        });
    }

    @SuppressLint("ShowToast")
    static void init(Context appContext) {
        context = appContext.getApplicationContext();
        toast = Toast.makeText(appContext, "", Toast.LENGTH_SHORT);
        sHandler = new Handler(appContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                synchronized (ToastMaker.class) {
                    switch (msg.what) {
                        default:
                            break;
                        case MAKE:
                            CharSequence message = (CharSequence) msg.obj;
                            toast.setText(message);
                            break;
                        case TEXT:
                            String text = (String) msg.obj;
                            toast.setText(text);
                            break;
                        case SHOW:
                            toast.show();
                            break;
                        case DURATION:
                            toast.setDuration(msg.arg1);
                            break;
                        case GRAVITY:
                            Bundle gravity = msg.getData();
                            toast.setGravity(gravity.getInt("gravity"), gravity.getInt("xOffset"), gravity.getInt("yOffset"));
                            break;
                        case VIEW:
                            View view = (View) msg.obj;
                            toast.setView(view);
                            break;
                        case MARGIN:
                            Bundle margin = msg.getData();
                            toast.setMargin(margin.getFloat("horizontalMargin"), margin.getFloat("verticalMargin"));
                            break;
                        case ANIMATION:
                            Bundle anim = msg.getData();
                            int animResId = anim.getInt("animResId", 0);
                            if (animResId != 0) {
                                initAnim(animResId);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static void showShort(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
//        make(message).show();
        float mb = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, Utils.getAppContext().getResources().getDisplayMetrics());
        AnyLayer.toast()
                .message(message)
                .removeOthers(true)
                .duration(2000)
                .gravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .backgroundDrawable(R.drawable.basic_ui_toast_bg)
                .marginBottom((int) mb)
                .marginLeft(0)
                .marginRight(0)
                .marginTop(0)
                .animator(new Layer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(View target) {
                        return AnimatorHelper.createBottomAlphaInAnim(target);
                    }

                    @Override
                    public Animator createOutAnimator(View target) {
                        return AnimatorHelper.createBottomAlphaOutAnim(target);
                    }
                })
                .show();
    }

    public static void showShort(@StringRes int message) {
        showShort(context.getString(message));
    }

    @SuppressLint("ShowToast")
    public static ToastMaker make(CharSequence message) {
        if (sHandler == null) {
            throw new ToastMakerNotInitException("未在Application中初始化");
        }
        if (message != null) {
            Message msg = sHandler.obtainMessage();
            msg.what = MAKE;
            msg.obj = message;
            sHandler.sendMessage(msg);
        }
        return Holder.INSTANCE;
    }

    private static void initAnim(int animResId) {
        try {
            Field mTNField = toast.getClass().getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(toast);
            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.windowAnimations = animResId;
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());
        } catch (Exception ignore) {
        }
    }

    public ToastMaker duration(int duration) {
        Message msg = sHandler.obtainMessage();
        msg.what = DURATION;
        msg.arg1 = duration;
        sHandler.sendMessage(msg);
        return this;
    }

    public ToastMaker view(View view) {
        Message msg = sHandler.obtainMessage();
        msg.what = VIEW;
        msg.obj = view;
        sHandler.sendMessage(msg);
        return this;
    }

    public ToastMaker text(String text) {
        Message msg = sHandler.obtainMessage();
        msg.what = TEXT;
        msg.obj = text;
        sHandler.sendMessage(msg);
        return this;
    }

    public ToastMaker gravity(int gravity, int xOffset, int yOffset) {
        Message msg = sHandler.obtainMessage();
        msg.what = GRAVITY;
        Bundle data = new Bundle(3);
        data.putInt("gravity", gravity);
        data.putInt("xOffset", xOffset);
        data.putInt("yOffset", yOffset);
        msg.setData(data);
        sHandler.sendMessage(msg);
        return this;
    }

    public ToastMaker margin(float horizontalMargin, float verticalMargin) {
        Message msg = sHandler.obtainMessage();
        msg.what = MARGIN;
        Bundle data = new Bundle(2);
        data.putFloat("horizontalMargin", horizontalMargin);
        data.putFloat("verticalMargin", verticalMargin);
        msg.setData(data);
        sHandler.sendMessage(msg);
        return this;
    }

    public ToastMaker animation(int animResId) {
        Message msg = sHandler.obtainMessage();
        msg.what = ANIMATION;
        Bundle data = new Bundle(1);
        data.putInt("animResId", animResId);
        msg.setData(data);
        sHandler.sendMessage(msg);
        return this;
    }

    public void show() {
        Message msg = sHandler.obtainMessage();
        msg.what = SHOW;
        sHandler.sendMessage(msg);
    }

    private static class Holder {
        private static ToastMaker INSTANCE = new ToastMaker();
    }

    private static class ToastMakerNotInitException extends RuntimeException {
        private ToastMakerNotInitException(String message) {
            super(message);
        }
    }
}
