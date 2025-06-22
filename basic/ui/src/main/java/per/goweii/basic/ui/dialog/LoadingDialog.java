package per.goweii.basic.ui.dialog;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.anylayer.utils.AnimatorHelper;
import per.goweii.basic.ui.R;

/**
 * loading弹窗
 *
 * @author Cuizhen
 * @date 2018/6/21-上午10:00
 */
public class LoadingDialog {

    private static final long ANIM_DURATION = 200;
    private final Context context;
    private DialogLayer mAnyDialog;
    private int count = 0;

    private LoadingDialog(Context context) {
        this.context = context;
    }

    public static LoadingDialog with(Context context) {
        return new LoadingDialog(context);
    }

    public void show() {
        if (count <= 0) {
            count = 0;
            mAnyDialog = AnyLayer.dialog(context)
                    .setContentView(R.layout.basic_ui_dialog_loading)
                    .setBackgroundColorInt(Color.TRANSPARENT)
                    .setCancelableOnTouchOutside(false)
                    .setCancelableOnClickKeyBack(false)
                    .setGravity(Gravity.CENTER)
                    .setContentAnimator(new Layer.AnimatorCreator() {
                        @Nullable
                        @Override
                        public Animator createInAnimator(@NonNull View target) {
                            return AnimatorHelper.createZoomInAnim(target).setDuration(ANIM_DURATION);
                        }

                        @Nullable
                        @Override
                        public Animator createOutAnimator(@NonNull View target) {
                            return AnimatorHelper.createZoomOutAnim(target).setDuration(ANIM_DURATION);
                        }
                    });
            mAnyDialog.show();
        }
        count++;
    }

    public void dismiss() {
        count--;
        if (count <= 0) {
            clear();
        }
    }

    public void clear() {
        if (mAnyDialog != null) {
            mAnyDialog.dismiss(false);
            mAnyDialog = null;
        }
        count = 0;
    }
}
