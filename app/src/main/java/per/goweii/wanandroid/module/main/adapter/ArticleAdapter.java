package per.goweii.wanandroid.module.main.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.basic.utils.listener.OnClickListener2;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.activity.UserPageActivity;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ArticleAdapter extends BaseQuickAdapter<ArticleBean, BaseViewHolder> {

    private OnItemChildViewClickListener mOnItemChildViewClickListener = null;

    public ArticleAdapter() {
        super(R.layout.rv_item_article);
    }

    public void setOnItemChildViewClickListener(OnItemChildViewClickListener onItemChildViewClickListener) {
        mOnItemChildViewClickListener = onItemChildViewClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, ArticleBean item) {
        helper.setText(R.id.tv_title, Html.fromHtml(item.getTitle()));
        helper.setText(R.id.tv_time, item.getNiceDate());
        helper.setText(R.id.tv_super_chapter_name, item.getSuperChapterName());
        helper.setText(R.id.tv_chapter_name, item.getChapterName());
        if (TextUtils.isEmpty(item.getSuperChapterName()) || TextUtils.isEmpty(item.getChapterName())) {
            helper.setGone(R.id.fl_dot, false);
        } else {
            helper.setGone(R.id.fl_dot, true);
        }
        TextView tv_author = helper.getView(R.id.tv_author);
        tv_author.setText(item.getAuthor());
        LinearLayout ll_new = helper.getView(R.id.ll_new);
        if (item.isFresh()) {
            ll_new.setVisibility(View.VISIBLE);
        } else {
            ll_new.setVisibility(View.GONE);
        }
        ImageView iv_img = helper.getView(R.id.iv_img);
        if (!TextUtils.isEmpty(item.getEnvelopePic())) {
            ImageLoader.image(iv_img, item.getEnvelopePic());
            iv_img.setVisibility(View.VISIBLE);
        } else {
            iv_img.setVisibility(View.GONE);
        }
        CollectView cv_collect = helper.getView(R.id.cv_collect);
        if (item.isCollect()) {
            cv_collect.setChecked(true);
        } else {
            cv_collect.setChecked(false);
        }
        TextView tv_tag = helper.getView(R.id.tv_tag);
        if (item.getTags() != null && item.getTags().size() > 0) {
            tv_tag.setText(item.getTags().get(0).getName());
            tv_tag.setVisibility(View.VISIBLE);
        } else {
            tv_tag.setVisibility(View.GONE);
        }
        tv_author.setOnClickListener(new OnClickListener2() {
            @Override
            public void onClick2(View v) {
                UserPageActivity.start(v.getContext(), item.getUserId());
            }
        });
        cv_collect.setOnClickListener(new CollectView.OnClickListener() {
            @Override
            public void onClick(CollectView v) {
                if (mOnItemChildViewClickListener != null) {
                    mOnItemChildViewClickListener.onCollectClick(helper, v, helper.getAdapterPosition() - getHeaderLayoutCount());
                }
            }
        });
    }

    public interface OnItemChildViewClickListener {
        void onCollectClick(BaseViewHolder helper, CollectView v, int position);
    }
}
