package per.goweii.wanandroid.module.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebShareDialog {

    public static void show(@NonNull Context context,
                            @NonNull OnShareClickListener listener) {
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_web_share)
                .gravity(Gravity.BOTTOM)
                .backgroundDimDefault()
                .dragDismiss(DragLayout.DragStyle.Bottom)
                .onClickToDismiss(new Layer.OnClickListener() {
                                      @Override
                                      public void onClick(Layer layer, View v) {
                                          switch (v.getId()) {
                                              default:
                                                  break;
                                              case R.id.dialog_web_share_iv_capture:
                                                  listener.onCapture();
                                                  break;
                                              case R.id.dialog_web_share_iv_qrcode:
                                                  listener.onQrcode();
                                                  break;
                                          }
                                      }
                                  },
                        R.id.dialog_web_share_iv_capture,
                        R.id.dialog_web_share_iv_qrcode,
                        R.id.dialog_web_share_iv_dismiss)
                .show();
    }

    public interface OnShareClickListener {
        void onCapture();

        void onQrcode();
    }

}
