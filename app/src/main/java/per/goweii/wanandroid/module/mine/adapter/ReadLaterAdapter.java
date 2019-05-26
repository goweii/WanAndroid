package per.goweii.wanandroid.module.mine.adapter;

import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.mine.model.ReadLaterEntity;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ReadLaterAdapter extends BaseQuickAdapter<ReadLaterEntity, BaseViewHolder> {

    private final SimpleDateFormat mSimpleDateFormat;

    public ReadLaterAdapter() {
        super(R.layout.rv_item_readlater);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected void convert(BaseViewHolder helper, ReadLaterEntity item) {
        helper.setText(R.id.tv_title, Html.fromHtml(item.getTitle()));
        String time = mSimpleDateFormat.format(new Date(item.getTime()));
        helper.setText(R.id.tv_time, time);
        helper.addOnClickListener(R.id.iv_remove);
    }
}
