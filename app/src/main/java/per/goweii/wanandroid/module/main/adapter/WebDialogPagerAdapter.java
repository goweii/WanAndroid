package per.goweii.wanandroid.module.main.adapter;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.utils.web.WebHolder;
import per.goweii.wanandroid.widget.WebContainer;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * GitHub: https://github.com/goweii
 */
public class WebDialogPagerAdapter extends PagerAdapter {

    private final Activity mActivity;
    private final List<ArticleBean> mTopUrls;
    private final List<MultiItemEntity> mUrls;
    private final SparseArray<WebHolder> mWebs = new SparseArray<>();

    private OnDoubleClickListener mOnDoubleClickListener = null;

    public WebDialogPagerAdapter(Activity activity, List<ArticleBean> topUrls, List<MultiItemEntity> urls) {
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

    public ArticleBean getArticleBean(int position) {
        MultiItemEntity entity = getBean(position);
        if (entity != null && entity.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
            return (ArticleBean) entity;
        }
        return null;
    }

    public MultiItemEntity getBean(int pos) {
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
        final MultiItemEntity data = getBean(position);
        final ArticleBean bean;
        if (data.getItemType() == ArticleAdapter.ITEM_TYPE_ARTICLE) {
            bean = (ArticleBean) data;
        } else {
            bean = null;
        }
        View rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_web_vp_item, container, false);
        WebContainer wc = rootView.findViewById(R.id.dialog_web_wc);
        wc.setOnDoubleClickListener(new WebContainer.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(float x, float y) {
                if (bean != null && mOnDoubleClickListener != null) {
                    mOnDoubleClickListener.onDoubleClick(bean);
                }
            }
        });
        WebHolder web = WebHolder.with(mActivity, wc).loadUrl(bean != null ? bean.getLink() : "");
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
