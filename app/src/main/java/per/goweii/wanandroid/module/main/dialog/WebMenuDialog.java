package per.goweii.wanandroid.module.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.module.mine.activity.HostInterruptActivity;
import per.goweii.wanandroid.module.mine.activity.SettingActivity;
import per.goweii.wanandroid.utils.HostInterceptUtils;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public class WebMenuDialog {

    public static void show(@NonNull Context context,
                            final boolean collected,
                            @NonNull OnMenuClickListener listener) {
        AnyLayer.dialog(context)
                .contentView(R.layout.dialog_web_menu)
                .backgroundDimDefault()
                .dragDismiss(DragLayout.DragStyle.Bottom)
                .gravity(Gravity.BOTTOM)
                .onClickToDismiss(new Layer.OnClickListener() {
                                      @Override
                                      public void onClick(Layer layer, View v) {
                                          switch (v.getId()) {
                                              default:
                                                  break;
                                              case R.id.dialog_web_menu_iv_share_article:
                                                  if (UserUtils.getInstance().doIfLogin(v.getContext())) {
                                                      listener.onShareArticle();
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
                                              case R.id.dialog_web_menu_iv_home:
                                                  listener.onHome();
                                                  break;
                                              case R.id.dialog_web_menu_iv_refresh:
                                                  listener.onRefresh();
                                                  break;
                                              case R.id.dialog_web_menu_iv_close_activity:
                                                  listener.onCloseActivity();
                                                  break;
                                              case R.id.dialog_web_menu_iv_setting:
                                                  SettingActivity.start(context);
                                                  break;
                                              case R.id.dialog_web_menu_iv_share:
                                                  listener.onShare();
                                                  break;
                                          }
                                      }
                                  },
                        R.id.dialog_web_menu_iv_share_article,
                        R.id.dialog_web_menu_iv_read_later,
                        R.id.dialog_web_menu_iv_home,
                        R.id.dialog_web_menu_iv_collect,
                        R.id.dialog_web_menu_iv_refresh,
                        R.id.dialog_web_menu_iv_close_activity,
                        R.id.dialog_web_menu_iv_dismiss,
                        R.id.dialog_web_menu_iv_setting,
                        R.id.dialog_web_menu_iv_share)
                .onClick(new Layer.OnClickListener() {
                             @Override
                             public void onClick(Layer layer, View v) {
                                 switch (v.getId()) {
                                     default:
                                         break;
                                     case R.id.dialog_web_menu_iv_interrupt:
                                         switch (SettingUtils.getInstance().getUrlInterceptType()) {
                                             default:
                                             case HostInterceptUtils.TYPE_NOTHING:
                                                 SettingUtils.getInstance().setUrlInterceptType(HostInterceptUtils.TYPE_ONLY_WHITE);
                                                 break;
                                             case HostInterceptUtils.TYPE_ONLY_WHITE:
                                                 SettingUtils.getInstance().setUrlInterceptType(HostInterceptUtils.TYPE_INTERCEPT_BLACK);
                                                 break;
                                             case HostInterceptUtils.TYPE_INTERCEPT_BLACK:
                                                 SettingUtils.getInstance().setUrlInterceptType(HostInterceptUtils.TYPE_NOTHING);
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
                        ImageView iv_collect = layer.getView(R.id.dialog_web_menu_iv_collect);
                        TextView tv_collect = layer.getView(R.id.dialog_web_menu_tv_collect);
                        switchCollectState(iv_collect, tv_collect, collected);
                        ImageView iv_interrupt = layer.getView(R.id.dialog_web_menu_iv_interrupt);
                        TextView tv_interrupt = layer.getView(R.id.dialog_web_menu_tv_interrupt);
                        switchInterruptState(iv_interrupt, tv_interrupt);
                        ImageView iv_swipe_back = layer.getView(R.id.dialog_web_menu_iv_swipe_back);
                        switchSwipeBackState(iv_swipe_back);
                        iv_interrupt.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                HostInterruptActivity.start(context);
                                layer.dismiss();
                                return true;
                            }
                        });
                    }
                })
                .show();
    }

    private static void switchCollectState(ImageView iv_collect, TextView tv_collect, boolean collected) {
        setIconChecked(iv_collect, collected);
        if (collected) {
            tv_collect.setText("已收藏");
        } else {
            tv_collect.setText("收藏");
        }
    }

    private static void switchInterruptState(ImageView iv_interrupt, TextView tv_interrupt) {
        switch (SettingUtils.getInstance().getUrlInterceptType()) {
            default:
            case HostInterceptUtils.TYPE_NOTHING:
                setIconChecked(iv_interrupt, false);
                tv_interrupt.setText(HostInterceptUtils.getName(HostInterceptUtils.TYPE_NOTHING));
                break;
            case HostInterceptUtils.TYPE_ONLY_WHITE:
                setIconChecked(iv_interrupt, true);
                tv_interrupt.setText(HostInterceptUtils.getName(HostInterceptUtils.TYPE_ONLY_WHITE));
                break;
            case HostInterceptUtils.TYPE_INTERCEPT_BLACK:
                setIconChecked(iv_interrupt, true);
                tv_interrupt.setText(HostInterceptUtils.getName(HostInterceptUtils.TYPE_INTERCEPT_BLACK));
                break;
        }
    }

    private static void switchSwipeBackState(ImageView iv_swipe_back) {
        setIconChecked(iv_swipe_back, SettingUtils.getInstance().isWebSwipeBackEdge());
    }

    private static void setIconChecked(ImageView iv, boolean checked) {
        if (checked) {
            iv.setBackgroundResource(R.drawable.bg_press_color_main_radius_max);
        } else {
            iv.setBackgroundResource(R.drawable.bg_press_color_surface_top_radius_max);
        }
    }

    public interface OnMenuClickListener {
        void onShareArticle();

        void onCollect();

        void onReadLater();

        void onHome();

        void onRefresh();

        void onCloseActivity();

        void onShare();
    }

}
