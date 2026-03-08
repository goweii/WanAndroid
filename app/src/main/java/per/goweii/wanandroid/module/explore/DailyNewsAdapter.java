package per.goweii.wanandroid.module.explore;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.web.cache.HtmlCacheManager;
import per.goweii.wanandroid.widget.BravhLoadMoreView;
import per.goweii.wanandroid.widget.CollectView;

public class DailyNewsAdapter extends BaseQuickAdapter<DailyNewsBean, BaseViewHolder> {
    public DailyNewsAdapter() {
        super(R.layout.rv_item_article, null);
        setLoadMoreView(new BravhLoadMoreView());
    }

    @Override
    public void setNewData(@Nullable List<DailyNewsBean> data) {
        setNewDiffData(new DailyNewsDiffCallback(data));
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DailyNewsBean item) {
        final View view = helper.itemView;
        HtmlCacheManager.INSTANCE.submit(item.getUrl());
        LinearLayout ll_top = view.findViewById(R.id.ll_top);
        LinearLayout ll_chapter = view.findViewById(R.id.ll_chapter);
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
        ll_chapter.setVisibility(View.GONE);
        ll_top.setVisibility(View.GONE);
        tv_chapter_name.setVisibility(View.GONE);
        cv_collect.setVisibility(View.GONE);
        tv_top.setVisibility(View.VISIBLE);
        tv_new.setVisibility(View.VISIBLE);
        tv_author.setText(DailyNewsPlatform.JUEJIN.getPlatformName());
        tv_tag.setVisibility(View.GONE);
        tv_chapter_name.setVisibility(View.GONE);
        tv_time.setVisibility(View.VISIBLE);
        tv_time.setText(item.getPublish_time());
        iv_img.setVisibility(View.GONE);
        tv_title.setText(Html.fromHtml(item.getTitle()));
        tv_desc.setVisibility(View.GONE);
    }
}
