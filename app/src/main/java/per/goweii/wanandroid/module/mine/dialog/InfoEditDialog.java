package per.goweii.wanandroid.module.mine.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
public class InfoEditDialog {

    public static InfoEditDialog with(Context context) {
        return new InfoEditDialog(context);
    }

    private final Context mContext;

    private String mTitle = null;
    private String mHint = null;
    private String mText = null;

    private InfoEditDialog(Context context) {
        mContext = context;
    }

    public InfoEditDialog title(String title) {
        mTitle = title;
        return this;
    }

    public InfoEditDialog hint(String hint) {
        mHint = hint;
        return this;
    }

    public InfoEditDialog text(String text) {
        mText = text;
        return this;
    }

    public void show(SimpleCallback<String> callback) {
        AnyLayer.dialog(mContext)
                .setContentView(R.layout.dialog_info_edit)
                .setSwipeDismiss(SwipeLayout.Direction.BOTTOM)
                .addSoftInputCompat(true)
                .setBackgroundDimDefault()
                .setCancelableOnClickKeyBack(true)
                .setCancelableOnTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .addOnBindDataListener(new Layer.OnBindDataListener() {
                    @Override
                    public void onBindData(@NonNull Layer layer) {
                        if (!TextUtils.isEmpty(mTitle)) {
                            TextView tv_title = layer.requireView(R.id.dialog_info_edit_tv_title);
                            tv_title.setText(mTitle);
                        }
                        if (!TextUtils.isEmpty(mHint) || !TextUtils.isEmpty(mText)) {
                            EditText et_input = layer.requireView(R.id.dialog_info_edit_et_input);
                            et_input.setText(mText);
                            et_input.setHint(mHint);
                        }
                    }
                })
                .addOnVisibleChangeListener(new Layer.OnVisibleChangedListener() {
                    @Override
                    public void onShow(@NonNull Layer layer) {
                        EditText et_input = layer.requireView(R.id.dialog_info_edit_et_input);
                        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!et_input.isFocused()) {
                                    InputMethodUtils.hide(et_input);
                                }
                            }
                        };
                        et_input.setOnFocusChangeListener(listener);
                    }

                    @Override
                    public void onDismiss(@NonNull Layer layer) {
                        EditText et_input = layer.requireView(R.id.dialog_info_edit_et_input);
                        InputMethodUtils.hide(et_input);
                    }
                })
                .addOnClickToDismissListener(R.id.dialog_info_edit_tv_no)
                .addOnClickToDismissListener(new Layer.OnClickListener() {
                    @Override
                    public void onClick(@NonNull Layer layer, @NonNull View v) {
                        EditText et_input = layer.requireView(R.id.dialog_info_edit_et_input);
                        if (callback != null) {
                            String str = et_input.getText().toString();
                            callback.onResult(str);
                        }
                    }
                }, R.id.dialog_info_edit_tv_yes)
                .show();
    }

}
