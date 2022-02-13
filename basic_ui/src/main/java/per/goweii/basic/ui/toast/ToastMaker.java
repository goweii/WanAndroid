package per.goweii.basic.ui.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.reflect.Field;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.ui.R;

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
    private static Context sContext = null;
    private static Handler sHandler = null;
    private static Toast sToast = null;

    @SuppressLint("ShowToast")
    public static void init(Context appContext) {
        sContext = appContext.getApplicationContext();
        sHandler = new Handler(appContext.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                sToast.show();
            }
        };
    }

    public static void showShort(@StringRes int message) {
        showShort(sContext.getString(message));
    }

    public static void showShort(CharSequence message) {
        if (TextUtils.isEmpty(message)) return;
        AnyLayer.toast()
                .setMessage(message)
                .setRemoveOthers(true)
                .setDuration(2000)
                .setGravity(Gravity.CENTER)
                .setContentView(R.layout.basic_ui_toast)
                .addOnBindDataListener(new Layer.OnBindDataListener() {
                    @Override
                    public void onBindData(@NonNull Layer layer) {
                        TextView textView = layer.requireView(R.id.basic_ui_toast_msg);
                        textView.setText(message);
                    }
                })
                .show();
    }

    @SuppressLint("ShowToast")
    public static ToastMaker make(CharSequence message) {
        if (sToast == null) {
            sToast = Toast.makeText(sContext, "", Toast.LENGTH_SHORT);
        }
        sToast.setText(message);
        return Holder.INSTANCE;
    }

    public ToastMaker duration(int duration) {
        sToast.setDuration(duration);
        return this;
    }

    @Deprecated
    public ToastMaker view(View view) {
        sToast.setView(view);
        return this;
    }

    public ToastMaker text(String text) {
        sToast.setText(text);
        return this;
    }

    public ToastMaker gravity(int gravity, int xOffset, int yOffset) {
        sToast.setGravity(gravity, xOffset, yOffset);
        return this;
    }

    public ToastMaker margin(float horizontalMargin, float verticalMargin) {
        sToast.setMargin(horizontalMargin, verticalMargin);
        return this;
    }

    public ToastMaker animation(int animResId) {
        if (animResId != 0) {
            initAnim(animResId);
        }
        return this;
    }

    public void show() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            sHandler.sendEmptyMessage(0);
        } else {
            sToast.show();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void initAnim(int animResId) {
        try {
            Field mTNField = sToast.getClass().getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(sToast);
            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.windowAnimations = animResId;
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, sToast.getView());
        } catch (Exception ignore) {
        }
    }

    private static class Holder {
        private static final ToastMaker INSTANCE = new ToastMaker();
    }
}
