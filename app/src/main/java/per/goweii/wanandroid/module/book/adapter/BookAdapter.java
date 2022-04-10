package per.goweii.wanandroid.module.book.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.book.model.BookBean;
import per.goweii.wanandroid.utils.ImageLoader;

public class BookAdapter extends BaseQuickAdapter<BookBean, BaseViewHolder> {
    public BookAdapter() {
        super(R.layout.rv_item_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, @NonNull BookBean item) {
        ImageLoader.image(helper.getView(R.id.piv_img), item.getCover());
    }
}
