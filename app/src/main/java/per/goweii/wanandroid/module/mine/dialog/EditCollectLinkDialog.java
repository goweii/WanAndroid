package per.goweii.wanandroid.module.mine.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.widget.SwipeLayout;
import per.goweii.basic.utils.EditTextUtils;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public class EditCollectLinkDialog {

    public static void show(Context context, CollectionLinkBean data, SimpleCallback<CollectionLinkBean> callback) {
        AnyLayer.dialog(context)
                .setContentView(R.layout.dialog_edit_collect_link)
                .setSwipeDismiss(SwipeLayout.Direction.BOTTOM)
                .addSoftInputCompat(true, R.id.dialog_edit_collect_link_et_title)
                .addSoftInputCompat(true, R.id.dialog_edit_collect_link_et_link)
                .setBackgroundDimDefault()
                .setCancelableOnClickKeyBack(true)
                .setCancelableOnTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .addOnBindDataListener(new Layer.OnBindDataListener() {
                    @Override
                    public void onBindData(@NonNull Layer layer) {
                        EditText et_title = layer.requireView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.requireView(R.id.dialog_edit_collect_link_et_link);
                        EditTextUtils.setTextWithSelection(et_title, data.getName());
                        EditTextUtils.setTextWithSelection(et_link, data.getLink());
                    }
                })
                .addOnVisibleChangeListener(new Layer.OnVisibleChangedListener() {
                    @Override
                    public void onShow(@NonNull Layer layer) {
                        EditText et_title = layer.requireView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.requireView(R.id.dialog_edit_collect_link_et_link);
                        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!et_title.isFocused() && !et_link.isFocused()) {
                                    InputMethodUtils.hide(et_title);
                                    InputMethodUtils.hide(et_link);
                                }
                            }
                        };
                        et_title.setOnFocusChangeListener(listener);
                        et_link.setOnFocusChangeListener(listener);
                    }

                    @Override
                    public void onDismiss(@NonNull Layer layer) {
                        EditText et_title = layer.requireView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.requireView(R.id.dialog_edit_collect_link_et_link);
                        InputMethodUtils.hide(et_title);
                        InputMethodUtils.hide(et_link);
                    }
                })
                .addOnClickToDismissListener(R.id.dialog_edit_collect_link_tv_no)
                .addOnClickToDismissListener(new Layer.OnClickListener() {
                    @Override
                    public void onClick(@NonNull Layer layer, @NonNull View v) {
                        EditText et_title = layer.requireView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.requireView(R.id.dialog_edit_collect_link_et_link);
                        if (callback != null) {
                            String title = et_title.getText().toString();
                            String link = et_link.getText().toString();
                            CollectionLinkBean bean = new CollectionLinkBean();
                            bean.setId(data.getId());
                            bean.setName(title);
                            bean.setLink(link);
                            callback.onResult(bean);
                        }
                    }
                }, R.id.dialog_edit_collect_link_tv_yes)
                .show();
    }

}
