package per.goweii.wanandroid.module.main.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.just.agentweb.AgentWeb;

import java.util.List;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.dialog.WebDialog;
import per.goweii.wanandroid.utils.AgentWebCreator;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebDialogPagerAdapter extends PagerAdapter {

    private final Activity mActivity;
    private final List<WebDialog.Data> mUrls;
    private final SparseArray<AgentWeb> mAgentWebs = new SparseArray<>();

    public WebDialogPagerAdapter(Activity activity, List<WebDialog.Data> urls) {
        mUrls = urls;
        mActivity = activity;
    }

    public AgentWeb getAgentWeb(int pos) {
        return mAgentWebs.get(pos);
    }

    @Override
    public int getCount() {
        return mUrls == null ? 0 : mUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_web_vp_item, container, false);
        WebContainer wc = rootView.findViewById(R.id.dialog_web_wc);
        AgentWeb agentWeb = AgentWebCreator.create(mActivity, wc, mUrls.get(position).getUrl());
        rootView.setTag(agentWeb);
        mAgentWebs.put(position, agentWeb);
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mAgentWebs.remove(position);
        View rootView = (View) object;
        Object tag = rootView.getTag();
        if (tag instanceof AgentWeb) {
            AgentWeb agentWeb = (AgentWeb) tag;
            agentWeb.getWebLifeCycle().onDestroy();
            rootView.setTag(null);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
