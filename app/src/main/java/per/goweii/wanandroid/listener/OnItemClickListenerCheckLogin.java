package per.goweii.wanandroid.listener;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public abstract class OnItemClickListenerCheckLogin implements BaseQuickAdapter.OnItemClickListener {

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (UserUtils.getInstance().doIfLogin(view.getContext())) {
            onItemClickCheckLogin(adapter, view, position);
        }
    }

    public abstract void onItemClickCheckLogin(BaseQuickAdapter adapter, View view, int position);
}
