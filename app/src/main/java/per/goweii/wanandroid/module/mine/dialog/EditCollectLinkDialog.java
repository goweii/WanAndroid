package per.goweii.wanandroid.module.mine.dialog;

import android.animation.Animator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.utils.EditTextUtils;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class EditCollectLinkDialog {

    public static void show(Context context, CollectionLinkBean data, SimpleCallback<CollectionLinkBean> callback){
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_edit_collect_link)
                .contentAnimator(new Layer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(View target) {
                        return AnimatorHelper.createTopInAnim(target);
                    }

                    @Override
                    public Animator createOutAnimator(View target) {
                        return AnimatorHelper.createTopOutAnim(target);
                    }
                })
                .asStatusBar(R.id.dialog_edit_collect_link_v_statusbar)
                .backgroundColorRes(R.color.dialog_bg)
                .cancelableOnClickKeyBack(true)
                .cancelableOnTouchOutside(true)
                .gravity(Gravity.TOP)
                .bindData(new Layer.DataBinder() {
                    @Override
                    public void bindData(Layer layer) {
                        EditText et_title = layer.getView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.getView(R.id.dialog_edit_collect_link_et_link);
                        EditTextUtils.setTextWithSelection(et_title, data.getName());
                        EditTextUtils.setTextWithSelection(et_link, data.getLink());
                    }
                })
                .onDismissListener(new Layer.OnDismissListener() {
                    @Override
                    public void onDismissing(Layer layer) {
                        EditText et_title = layer.getView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.getView(R.id.dialog_edit_collect_link_et_link);
                        InputMethodUtils.hide(et_title);
                        InputMethodUtils.hide(et_link);
                    }

                    @Override
                    public void onDismissed(Layer layer) {

                    }
                })
                .onClickToDismiss(R.id.dialog_edit_collect_link_tv_no)
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        EditText et_title = layer.getView(R.id.dialog_edit_collect_link_et_title);
                        EditText et_link = layer.getView(R.id.dialog_edit_collect_link_et_link);
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
