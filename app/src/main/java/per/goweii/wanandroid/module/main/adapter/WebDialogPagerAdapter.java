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
import per.goweii.wanandroid.module.main.model.ArticleBean;
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
    private final List<ArticleBean> mTopUrls;
    private final List<ArticleBean> mUrls;
    private final SparseArray<AgentWeb> mAgentWebs = new SparseArray<>();

    private OnDoubleClickListener mOnDoubleClickListener = null;

    public WebDialogPagerAdapter(Activity activity, List<ArticleBean> topUrls, List<ArticleBean> urls) {
        mTopUrls = topUrls;
        mUrls = urls;
        mActivity = activity;
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }

    public AgentWeb getAgentWeb(int pos) {
        return mAgentWebs.get(pos);
    }

    public void resumeAndPauseOthersAgentWeb(int pos) {
        for (int i = 0; i < mAgentWebs.size(); i++) {
            int index = mAgentWebs.keyAt(i);
            AgentWeb agentWeb = mAgentWebs.valueAt(i);
            if (agentWeb == null) {
                continue;
            }
            if (index == pos) {
                agentWeb.getWebLifeCycle().onResume();
            } else {
                agentWeb.getWebLifeCycle().onPause();
            }
        }
    }

    public void pauseAllAgentWeb() {
        for (int i = 0; i < mAgentWebs.size(); i++) {
            AgentWeb agentWeb = mAgentWebs.valueAt(i);
            if (agentWeb == null) {
                continue;
            }
            agentWeb.getWebLifeCycle().onPause();
        }
    }

    public void destroyAllAgentWeb() {
        for (int i = 0; i < mAgentWebs.size(); i++) {
            AgentWeb agentWeb = mAgentWebs.valueAt(i);
            if (agentWeb != null) {
                agentWeb.getWebLifeCycle().onDestroy();
            }
        }
    }

    public ArticleBean getBean(int pos) {
        int topUrlCount = mTopUrls == null ? 0 : mTopUrls.size();
        if (pos < topUrlCount) {
            return mTopUrls.get(pos);
        }
        return mUrls.get(pos - topUrlCount);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mTopUrls != null) {
            count += mTopUrls.size();
        }
        if (mUrls != null) {
            count += mUrls.size();
        }
        return count;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final ArticleBean data = getBean(position);
        View rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_web_vp_item, container, false);
        WebContainer wc = rootView.findViewById(R.id.dialog_web_wc);
        wc.setOnDoubleClickListener(new WebContainer.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(float x, float y) {
                if (mOnDoubleClickListener != null) {
                    mOnDoubleClickListener.onDoubleClick(data);
                }
            }
        });
        AgentWeb agentWeb = AgentWebCreator.create(mActivity, wc, data.getLink());
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

    public interface OnDoubleClickListener {
        void onDoubleClick(ArticleBean data);
    }
}
