package per.goweii.basic.ui.dialog;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import per.goweii.anydialog.AnimHelper;
import per.goweii.anydialog.AnyDialog;
import per.goweii.anydialog.IAnim;
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
    private AnyDialog mAnyDialog;
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
            mAnyDialog = AnyDialog.with(context)
                    .contentView(R.layout.basic_ui_dialog_loading)
                    .backgroundColorInt(Color.TRANSPARENT)
                    .cancelableOnClickKeyBack(false)
                    .cancelableOnTouchOutside(false)
                    .gravity(Gravity.CENTER)
                    .contentAnim(new IAnim() {
                        @Override
                        public Animator inAnim(View target) {
                            return AnimHelper.createZoomInAnim(target).setDuration(ANIM_DURATION);
                        }

                        @Override
                        public Animator outAnim(View target) {
                            return AnimHelper.createZoomOutAnim(target).setDuration(ANIM_DURATION);
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
            mAnyDialog.dismiss();
            mAnyDialog = null;
        }
        count = 0;
    }
}
