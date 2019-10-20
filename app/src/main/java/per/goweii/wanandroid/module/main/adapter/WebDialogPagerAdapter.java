package per.goweii.wanandroid.module.main.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.utils.WebHolder;
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
    private final SparseArray<WebHolder> mWebs = new SparseArray<>();

    private OnDoubleClickListener mOnDoubleClickListener = null;

    public WebDialogPagerAdapter(Activity activity, List<ArticleBean> topUrls, List<ArticleBean> urls) {
        mTopUrls = topUrls;
        mUrls = urls;
        mActivity = activity;
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }

    public WebHolder getWeb(int pos) {
        return mWebs.get(pos);
    }

    public void resumeAndPauseOthersAgentWeb(int pos) {
        for (int i = 0; i < mWebs.size(); i++) {
            int index = mWebs.keyAt(i);
            WebHolder web = mWebs.valueAt(i);
            if (web == null) {
                continue;
            }
            if (index == pos) {
                web.onResume();
            } else {
                web.onPause();
            }
        }
    }

    public void pauseAllAgentWeb() {
        for (int i = 0; i < mWebs.size(); i++) {
            WebHolder web = mWebs.valueAt(i);
            if (web == null) {
                continue;
            }
            web.onPause();
        }
    }

    public void destroyAllAgentWeb() {
        for (int i = 0; i < mWebs.size(); i++) {
            WebHolder web = mWebs.valueAt(i);
            if (web != null) {
                web.onDestroy();
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
        WebHolder web = WebHolder.with(mActivity, wc).loadUrl(data.getLink());
        mWebs.put(position, web);
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        WebHolder web = mWebs.get(position);
        web.onDestroy();
        mWebs.remove(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    public interface OnDoubleClickListener {
        void onDoubleClick(ArticleBean data);
    }
}
