package per.goweii.wanandroid.module.main.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Collection;
import java.util.List;

import per.goweii.basic.utils.StringUtils;
import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.module.home.activity.UserPageActivity;
import per.goweii.wanandroid.module.knowledge.activity.KnowledgeArticleActivity;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.utils.ArticleDiffCallback;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.UrlOpenUtils;
import per.goweii.wanandroid.utils.web.cache.HtmlCacheManager;
import per.goweii.wanandroid.widget.BravhLoadMoreView;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class ArticleAdapter extends BaseQuickAdapter<ArticleBean, BaseViewHolder> {

    private OnItemChildViewClickListener mOnItemChildViewClickListener = null;

    public ArticleAdapter() {
        super(0);
        setLoadMoreView(new BravhLoadMoreView());
        mLayoutResId = getArticleLayoutId();
    }

    protected int getArticleLayoutId() {
        return R.layout.rv_item_article;
    }

    public void setOnItemChildViewClickListener(OnItemChildViewClickListener onItemChildViewClickListener) {
        mOnItemChildViewClickListener = onItemChildViewClickListener;
    }

    @Override
    public void setNewData(@Nullable List<ArticleBean> data) {
        if (data == null || data.isEmpty()) {
            setNewData(null, false);
        } else {
            setNewData(data, true);
        }
    }

    public void setNewData(@Nullable List<ArticleBean> data, boolean useDiff) {
        if (useDiff) {
            boolean top = getRecyclerView().canScrollVertically(-1);
            setNewDiffData(data);
            if (!top) getRecyclerView().scrollToPosition(0);
        } else {
            super.setNewData(data);
        }
    }

    private void setNewDiffData(@Nullable List<ArticleBean> data) {
        setNewDiffData(new ArticleDiffCallback(data));
    }

    @Override
    public void addData(@NonNull Collection<? extends ArticleBean> newData) {
        super.addData(newData);
    }

    public void notifyAllUnCollect() {
        forEach(new ArticleAdapter.ArticleForEach() {
            @Override
            public boolean forEach(int dataPos, int adapterPos, ArticleBean bean) {
                if (bean.isCollect()) {
                    bean.setCollect(false);
                    notifyItemChanged(adapterPos);
                }
                return false;
            }
        });
    }

    public void notifyCollectionEvent(CollectionEvent event) {
        forEach(new ArticleAdapter.ArticleForEach() {
            @Override
            public boolean forEach(int dataPos, int adapterPos, ArticleBean bean) {
                if (bean.getId() == event.getArticleId()) {
                    if (bean.isCollect() != event.isCollect()) {
                        bean.setCollect(event.isCollect());
                        notifyItemChanged(adapterPos);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void forEach(@NonNull ArticleForEach articleForEach) {
        List<ArticleBean> list = getData();
        for (int i = 0; i < list.size(); i++) {
            ArticleBean item = list.get(i);
            if (articleForEach.forEach(i, i + getHeaderLayoutCount(), item)) {
                break;
            }
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ArticleBean item) {
        bindArticle(helper.itemView, item, new OnCollectListener() {
            @Override
            public void collect(ArticleBean item, CollectView v) {
                if (mOnItemChildViewClickListener != null) {
                    mOnItemChildViewClickListener.onCollectClick(helper, v, helper.getAdapterPosition() - getHeaderLayoutCount());
                }
            }

            @Override
            public void uncollect(ArticleBean item, CollectView v) {
                if (mOnItemChildViewClickListener != null) {
                    mOnItemChildViewClickListener.onCollectClick(helper, v, helper.getAdapterPosition() - getHeaderLayoutCount());
                }
            }
        });
    }

    public interface OnItemChildViewClickListener {
        void onCollectClick(BaseViewHolder helper, CollectView v, int position);
    }

    public interface OnCollectListener {
        void collect(ArticleBean item, CollectView v);

        void uncollect(ArticleBean item, CollectView v);
    }

    public static void bindArticle(View view, ArticleBean item, OnCollectListener onCollectListener) {
        HtmlCacheManager.INSTANCE.submit(item.getLink());
        TextView tv_top = view.findViewById(R.id.tv_top);
        TextView tv_new = view.findViewById(R.id.tv_new);
        TextView tv_author = view.findViewById(R.id.tv_author);
        TextView tv_tag = view.findViewById(R.id.tv_tag);
        TextView tv_time = view.findViewById(R.id.tv_time);
        ImageView iv_img = view.findViewById(R.id.iv_img);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_desc = view.findViewById(R.id.tv_desc);
        TextView tv_chapter_name = view.findViewById(R.id.tv_chapter_name);
        CollectView cv_collect = view.findViewById(R.id.cv_collect);
        if (item.isTop()) {
            tv_top.setVisibility(View.VISIBLE);
        } else {
            tv_top.setVisibility(View.GONE);
        }
        if (item.isFresh()) {
            tv_new.setVisibility(View.VISIBLE);
        } else {
            tv_new.setVisibility(View.GONE);
        }
        tv_author.setText(item.getAuthor());
        if (item.getTags() != null && item.getTags().size() > 0) {
            tv_tag.setText(item.getTags().get(0).getName());
            tv_tag.setVisibility(View.VISIBLE);
            tv_tag.setOnClickListener(new OnClickListener2() {
                @Override
                public void onClick2(View v) {
                    KnowledgeArticleActivity.start(v.getContext(), item.getTags().get(0));
                }
            });
        } else {
            tv_tag.setVisibility(View.GONE);
        }
        tv_time.setText(item.getNiceDate());
        if (!TextUtils.isEmpty(item.getEnvelopePic())) {
            ImageLoader.image(iv_img, item.getEnvelopePic());
            iv_img.setVisibility(View.VISIBLE);
        } else {
            iv_img.setVisibility(View.GONE);
        }
        tv_title.setText(Html.fromHtml(item.getTitle()));
        if (TextUtils.isEmpty(item.getDesc())) {
            tv_desc.setVisibility(View.GONE);
            tv_title.setSingleLine(false);
        } else {
            tv_desc.setVisibility(View.VISIBLE);
            tv_title.setSingleLine(true);
            String desc = Html.fromHtml(item.getDesc()).toString();
            desc = StringUtils.removeAllBank(desc, 2);
            tv_desc.setText(desc);
        }
        tv_chapter_name.setText(Html.fromHtml(formatChapterName(item.getSuperChapterName(), item.getChapterName())));
        if (item.isCollect()) {
            cv_collect.setChecked(true, false);
        } else {
            cv_collect.setChecked(false, false);
        }
        tv_chapter_name.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                KnowledgeArticleActivity.start(v.getContext(),
                        item.getSuperChapterId(), item.getSuperChapterName(),
                        item.getChapterId());
            }
        });
        tv_author.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                UserPageActivity.start(v.getContext(), item.getUserId());
            }
        });
        cv_collect.setOnClickListener(new CollectView.OnClickListener() {
            @Override
            public void onClick(CollectView v) {
                if (v.isChecked()) {
                    if (onCollectListener != null) {
                        onCollectListener.collect(item, v);
                    }
                } else {
                    if (onCollectListener != null) {
                        onCollectListener.uncollect(item, v);
                    }
                }
            }
        });
        view.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                UrlOpenUtils.Companion.with(item).open(v.getContext());
            }
        });
    }

    private static String formatChapterName(String... names) {
        StringBuilder format = new StringBuilder();
        for (String name : names) {
            if (!TextUtils.isEmpty(name)) {
                if (format.length() > 0) {
                    format.append("·");
                }
                format.append(name);
            }
        }
        return format.toString();
    }

    public interface ArticleForEach {
        boolean forEach(int dataPos, int adapterPos, ArticleBean bean);
    }
}
