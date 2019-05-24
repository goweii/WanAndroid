package per.goweii.wanandroid.module.mine.adapter;

import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionLinkAdapter extends BaseQuickAdapter<CollectionLinkBean, BaseViewHolder> {

    public CollectionLinkAdapter() {
        super(R.layout.rv_item_collection_link);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectionLinkBean item) {
        helper.setText(R.id.tv_title, Html.fromHtml(item.getName()));
        helper.setText(R.id.tv_url, item.getLink());
        helper.addOnClickListener(R.id.iv_remove);
    }
}
