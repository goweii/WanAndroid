package per.goweii.wanandroid.module.book.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.book.bean.BookIntroBean;

public class BookChapterAdapter extends BaseQuickAdapter<BookIntroBean.BookChapterBean, BaseViewHolder> {
    public BookChapterAdapter() {
        super(R.layout.rv_item_book_chapter);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, @NonNull BookIntroBean.BookChapterBean item) {
        helper.setText(R.id.tv_index, item.getIndex() + "");
        helper.setText(R.id.tv_title, item.getName());
    }
}
