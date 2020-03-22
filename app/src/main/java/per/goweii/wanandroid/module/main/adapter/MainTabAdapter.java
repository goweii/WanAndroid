package per.goweii.wanandroid.module.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import per.goweii.basic.core.adapter.TabFragmentPagerAdapter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.ScrollTop;
import per.goweii.wanandroid.module.main.model.TabEntity;

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
public class MainTabAdapter implements TabFragmentPagerAdapter.Page.TabAdapter<TabEntity> {
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindData(@NonNull View view, @NonNull TabEntity data, boolean selected) {
        Context context = view.getContext();
        ImageView iv_tab_icon = view.findViewById(R.id.iv_tab_icon);
        TextView tv_tab_name = view.findViewById(R.id.tv_tab_name);
        TextView tv_tab_msg = view.findViewById(R.id.tv_tab_msg);
        iv_tab_icon.setImageResource(data.getTabIcon());
        tv_tab_name.setText(data.getTabName());
        if (selected) {
            iv_tab_icon.setColorFilter(ContextCompat.getColor(context, R.color.main));
            tv_tab_name.setTextColor(ContextCompat.getColor(context, R.color.main));
        } else {
            iv_tab_icon.setColorFilter(ContextCompat.getColor(context, R.color.third));
            tv_tab_name.setTextColor(ContextCompat.getColor(context, R.color.third));
        }
        if (data.getMsgCount() > 0) {
            tv_tab_msg.setVisibility(View.VISIBLE);
            if (data.getMsgCount() > 99) {
                tv_tab_msg.setText("99+");
            } else {
                tv_tab_msg.setText(data.getMsgCount() + "");
            }
        } else {
            tv_tab_msg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDoubleTap(Fragment fragment) {
        if (fragment instanceof ScrollTop) {
            ScrollTop scrollTop = (ScrollTop) fragment;
            scrollTop.scrollTop();
        }
    }
}
