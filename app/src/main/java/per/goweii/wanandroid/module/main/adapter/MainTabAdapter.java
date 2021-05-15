package per.goweii.wanandroid.module.main.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import per.goweii.basic.core.adapter.TabFragmentPagerAdapter;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.module.main.model.TabEntity;
import per.goweii.wanandroid.utils.RvScrollTopUtils;

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
public class MainTabAdapter implements TabFragmentPagerAdapter.Page.TabAdapter<TabEntity> {
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindData(@NonNull View view, @NonNull TabEntity data, boolean selected) {
        ImageView iv_tab_icon = view.findViewById(R.id.iv_tab_icon);
        TextView tv_tab_name = view.findViewById(R.id.tv_tab_name);
        TextView tv_tab_msg = view.findViewById(R.id.tv_tab_msg);
        iv_tab_icon.setImageResource(data.getTabIcon());
        tv_tab_name.setText(data.getTabName());
        if (selected) {
            iv_tab_icon.setColorFilter(ResUtils.getThemeColor(iv_tab_icon, R.attr.colorIconMain));
            tv_tab_name.setTextColor(ResUtils.getThemeColor(tv_tab_name, R.attr.colorTextMain));
        } else {
            iv_tab_icon.setColorFilter(ResUtils.getThemeColor(iv_tab_icon, R.attr.colorIconThird));
            tv_tab_name.setTextColor(ResUtils.getThemeColor(tv_tab_name, R.attr.colorTextThird));
        }
        if (data.getMsgCount() > 0) {
            tv_tab_msg.setVisibility(View.VISIBLE);
            if (data.getMsgCount() > Config.NOTIFICATION_MAX_SHOW_COUNT) {
                tv_tab_msg.setText(Config.NOTIFICATION_MAX_SHOW_COUNT + "+");
            } else {
                tv_tab_msg.setText(data.getMsgCount() + "");
            }
        } else {
            tv_tab_msg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDoubleTap(Fragment fragment) {
        if (fragment instanceof RvScrollTopUtils.ScrollTop) {
            RvScrollTopUtils.ScrollTop scrollTop = (RvScrollTopUtils.ScrollTop) fragment;
            scrollTop.scrollTop();
        }
    }
}
