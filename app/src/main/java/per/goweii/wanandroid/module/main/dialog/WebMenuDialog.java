package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.WebUrlInterceptUtils;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebMenuDialog {

    public static void show(@NonNull Context context, @NonNull OnMenuClickListener listener) {
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_web_menu)
                .gravity(Gravity.BOTTOM)
                .backgroundColorRes(R.color.dialog_bg)
                .contentAnimator(new Layer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(View target) {
                        return AnimatorHelper.createBottomInAnim(target);
                    }

                    @Override
                    public Animator createOutAnimator(View target) {
                        return AnimatorHelper.createBottomOutAnim(target);
                    }
                })
                .onClickToDismiss(new Layer.OnClickListener() {
                                      @Override
                                      public void onClick(Layer layer, View v) {
                                          switch (v.getId()) {
                                              default:
                                                  break;
                                              case R.id.dialog_web_menu_iv_share:
                                                  if (UserUtils.getInstance().doIfLogin(v.getContext())) {
                                                      listener.onShare();
                                                  }
                                                  break;
                                              case R.id.dialog_web_menu_iv_collect:
                                                  if (UserUtils.getInstance().doIfLogin(v.getContext())) {
                                                      listener.onCollect();
                                                  }
                                                  break;
                                              case R.id.dialog_web_menu_iv_read_later:
                                                  listener.onReadLater();
                                                  break;
                                              case R.id.dialog_web_menu_iv_browser:
                                                  listener.onBrowser();
                                                  break;
                                              case R.id.dialog_web_menu_iv_copy_link:
                                                  listener.onCopyLink();
                                                  break;
                                              case R.id.dialog_web_menu_iv_close_activity:
                                                  listener.onCloseActivity();
                                                  break;
                                          }
                                      }
                                  },
                        R.id.dialog_web_menu_iv_share,
                        R.id.dialog_web_menu_iv_read_later,
                        R.id.dialog_web_menu_iv_browser,
                        R.id.dialog_web_menu_iv_collect,
                        R.id.dialog_web_menu_iv_copy_link,
                        R.id.dialog_web_menu_iv_close_activity)
                .onClick(new Layer.OnClickListener() {
                             @Override
                             public void onClick(Layer layer, View v) {
                                 switch (v.getId()) {
                                     default:
                                         break;
                                     case R.id.dialog_web_menu_iv_interrupt:
                                         switch (SettingUtils.getInstance().getUrlInterceptType()) {
                                             default:
                                             case WebUrlInterceptUtils.TYPE_NOTHING:
                                                 SettingUtils.getInstance().setUrlInterceptType(WebUrlInterceptUtils.TYPE_ONLY_WHITE);
                                                 break;
                                             case WebUrlInterceptUtils.TYPE_ONLY_WHITE:
                                                 SettingUtils.getInstance().setUrlInterceptType(WebUrlInterceptUtils.TYPE_INTERCEPT_BLACK);
                                                 break;
                                             case WebUrlInterceptUtils.TYPE_INTERCEPT_BLACK:
                                                 SettingUtils.getInstance().setUrlInterceptType(WebUrlInterceptUtils.TYPE_NOTHING);
                                                 break;
                                         }
                                         ImageView iv_interrupt = layer.getView(R.id.dialog_web_menu_iv_interrupt);
                                         TextView tv_interrupt = layer.getView(R.id.dialog_web_menu_tv_interrupt);
                                         switchInterruptState(iv_interrupt, tv_interrupt);
                                         break;
                                     case R.id.dialog_web_menu_iv_swipe_back:
                                         boolean webSwipeBackEdge = !SettingUtils.getInstance().isWebSwipeBackEdge();
                                         SettingUtils.getInstance().setWebSwipeBackEdge(webSwipeBackEdge);
                                         if (context instanceof WebActivity) {
                                             WebActivity webActivity = (WebActivity) context;
                                             webActivity.refreshSwipeBackOnlyEdge();
                                         }
                                         ImageView iv_swipe_back = layer.getView(R.id.dialog_web_menu_iv_swipe_back);
                                         switchSwipeBackState(iv_swipe_back);
                                         break;
                                 }
                             }
                         },
                        R.id.dialog_web_menu_iv_interrupt,
                        R.id.dialog_web_menu_iv_swipe_back)
                .bindData(new Layer.DataBinder() {
                    @Override
                    public void bindData(Layer layer) {
                        ImageView iv_interrupt = layer.getView(R.id.dialog_web_menu_iv_interrupt);
                        TextView tv_interrupt = layer.getView(R.id.dialog_web_menu_tv_interrupt);
                        switchInterruptState(iv_interrupt, tv_interrupt);
                        ImageView iv_swipe_back = layer.getView(R.id.dialog_web_menu_iv_swipe_back);
                        switchSwipeBackState(iv_swipe_back);
                    }
                })
                .show();
    }

    private static void switchInterruptState(ImageView iv_interrupt, TextView tv_interrupt) {
        switch (SettingUtils.getInstance().getUrlInterceptType()) {
            default:
            case WebUrlInterceptUtils.TYPE_NOTHING:
                iv_interrupt.setBackgroundResource(R.drawable.bg_press_color_background_radius_max);
                tv_interrupt.setText(WebUrlInterceptUtils.getName(WebUrlInterceptUtils.TYPE_NOTHING));
                break;
            case WebUrlInterceptUtils.TYPE_ONLY_WHITE:
                iv_interrupt.setBackgroundResource(R.drawable.bg_press_color_main_radius_max);
                tv_interrupt.setText(WebUrlInterceptUtils.getName(WebUrlInterceptUtils.TYPE_ONLY_WHITE));
                break;
            case WebUrlInterceptUtils.TYPE_INTERCEPT_BLACK:
                iv_interrupt.setBackgroundResource(R.drawable.bg_press_color_main_radius_max);
                tv_interrupt.setText(WebUrlInterceptUtils.getName(WebUrlInterceptUtils.TYPE_INTERCEPT_BLACK));
                break;
        }
    }

    private static void switchSwipeBackState(ImageView iv_swipe_back) {
        if (SettingUtils.getInstance().isWebSwipeBackEdge()) {
            iv_swipe_back.setBackgroundResource(R.drawable.bg_press_color_main_radius_max);
        } else {
            iv_swipe_back.setBackgroundResource(R.drawable.bg_press_color_background_radius_max);
        }
    }

    public interface OnMenuClickListener {
        void onShare();

        void onCollect();

        void onReadLater();

        void onBrowser();

        void onCopyLink();

        void onCloseActivity();
    }

}
