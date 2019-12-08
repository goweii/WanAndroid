package per.goweii.wanandroid.module.mine.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AddHostDialog {

    public static void show(Context context, SimpleCallback<String> callback) {
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_add_host)
                .dragDismiss(DragLayout.DragStyle.Bottom)
                .backgroundDimDefault()
                .cancelableOnClickKeyBack(true)
                .cancelableOnTouchOutside(true)
                .gravity(Gravity.BOTTOM)
                .onVisibleChangeListener(new Layer.OnVisibleChangeListener() {
                    @Override
                    public void onShow(Layer layer) {
                        DialogLayer dialogLayer = (DialogLayer) layer;
                        EditText et_host = layer.getView(R.id.dialog_add_host_et_host);
                        dialogLayer.compatSoftInput(et_host);
                        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!et_host.isFocused()) {
                                    InputMethodUtils.hide(et_host);
                                }
                            }
                        };
                        et_host.setOnFocusChangeListener(listener);
                    }

                    @Override
                    public void onDismiss(Layer layer) {
                        DialogLayer dialogLayer = (DialogLayer) layer;
                        dialogLayer.removeSoftInput();
                        EditText et_host = layer.getView(R.id.dialog_add_host_et_host);
                        InputMethodUtils.hide(et_host);
                    }
                })
                .onClickToDismiss(R.id.dialog_add_host_tv_no)
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        EditText et_host = layer.getView(R.id.dialog_add_host_et_host);
                        if (callback != null) {
                            String host = et_host.getText().toString();
                            callback.onResult(host);
                        }
                    }
                }, R.id.dialog_add_host_tv_yes)
                .show();
    }

}
