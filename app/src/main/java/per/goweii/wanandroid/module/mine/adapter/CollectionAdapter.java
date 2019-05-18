package per.goweii.wanandroid.module.mine.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.mine.model.CollectionBean;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.widget.CollectView;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionAdapter extends BaseQuickAdapter<CollectionBean.DatasBean, BaseViewHolder> {

    private OnCollectViewClickListener mOnCollectViewClickListener = null;

    public CollectionAdapter() {
        super(R.layout.rv_item_article);
    }

    public void setOnCollectViewClickListener(OnCollectViewClickListener onCollectViewClickListener) {
        mOnCollectViewClickListener = onCollectViewClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectionBean.DatasBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_author, item.getAuthor());
        helper.setText(R.id.tv_time, item.getNiceDate());
        TextView tv_super_chapter_name = helper.getView(R.id.tv_super_chapter_name);
        tv_super_chapter_name.setVisibility(View.GONE);
        FrameLayout fl_dot = helper.getView(R.id.fl_dot);
        fl_dot.setVisibility(View.GONE);
        helper.setText(R.id.tv_chapter_name, item.getChapterName());
        LinearLayout ll_new = helper.getView(R.id.ll_new);
        ll_new.setVisibility(View.GONE);
        ImageView iv_img = helper.getView(R.id.iv_img);
        if (!TextUtils.isEmpty(item.getEnvelopePic())) {
            ImageLoader.image(iv_img, item.getEnvelopePic());
            iv_img.setVisibility(View.VISIBLE);
        } else {
            iv_img.setVisibility(View.GONE);
        }
        CollectView cv_collect = helper.getView(R.id.cv_collect);
        cv_collect.setChecked(true);
        TextView tv_tag = helper.getView(R.id.tv_tag);
        tv_tag.setVisibility(View.GONE);
        cv_collect.setOnClickListener(new CollectView.OnClickListener() {
            @Override
            public void onClick(CollectView v) {
                if (mOnCollectViewClickListener != null) {
                    mOnCollectViewClickListener.onClick(helper, v, helper.getAdapterPosition() - getHeaderLayoutCount());
                }
            }
        });
    }

    public interface OnCollectViewClickListener {
        void onClick(BaseViewHolder helper, CollectView v, int position);
    }
}
