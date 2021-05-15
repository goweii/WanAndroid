package per.goweii.wanandroid.module.main.adapter;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.db.model.ReadLaterModel;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class BookmarkAdapter extends BaseQuickAdapter<ReadLaterModel, BaseViewHolder> {

    private final SimpleDateFormat mSimpleDateFormat;

    @SuppressLint("SimpleDateFormat")
    public BookmarkAdapter() {
        super(R.layout.rv_item_bookmark);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ReadLaterModel item) {
        if (TextUtils.isEmpty(item.getTitle())) {
            helper.setText(R.id.tv_title, item.getLink());
        } else {
            helper.setText(R.id.tv_title, Html.fromHtml(item.getTitle()));
        }
        helper.setText(R.id.tv_title, Html.fromHtml(item.getTitle()));
        String time = mSimpleDateFormat.format(new Date(item.getTime()));
        helper.setText(R.id.tv_time, time);
        helper.addOnClickListener(R.id.rl_top, R.id.tv_delete, R.id.tv_open, R.id.tv_copy);
    }
}
