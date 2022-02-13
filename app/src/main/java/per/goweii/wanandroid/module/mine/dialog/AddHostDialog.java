package per.goweii.wanandroid.module.mine.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.widget.SwipeLayout;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public class AddHostDialog {

    public static void show(Context context, SimpleCallback<String> callback) {
        AnyLayer.dialog(context)
                .setContentView(R.layout.dialog_add_host)
                .addSoftInputCompat(true, R.id.dialog_add_host_et_host)
                .setSwipeDismiss(SwipeLayout.Direction.BOTTOM)
                .setBackgroundDimDefault()
                .setCancelableOnClickKeyBack(true)
                .setCancelableOnTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .addOnVisibleChangeListener(new Layer.OnVisibleChangedListener() {
                    @Override
                    public void onShow(@NonNull Layer layer) {
                        EditText et_host = layer.requireView(R.id.dialog_add_host_et_host);
                        et_host.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!et_host.isFocused()) {
                                    InputMethodUtils.hide(et_host);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDismiss(@NonNull Layer layer) {
                        EditText et_host = layer.requireView(R.id.dialog_add_host_et_host);
                        InputMethodUtils.hide(et_host);
                    }
                })
                .addOnClickToDismissListener(R.id.dialog_add_host_tv_no)
                .addOnClickToDismissListener(new Layer.OnClickListener() {
                    @Override
                    public void onClick(@NonNull Layer layer, @NonNull View v) {
                        EditText et_host = layer.requireView(R.id.dialog_add_host_et_host);
                        if (callback != null) {
                            String host = et_host.getText().toString();
                            callback.onResult(host);
                        }
                    }
                }, R.id.dialog_add_host_tv_yes)
                .show();
    }

}
