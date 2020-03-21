package per.goweii.wanandroid.module.mine.adapter;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.db.model.ReadRecordModel;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class ReadRecordAdapter extends BaseQuickAdapter<ReadRecordModel, BaseViewHolder> {

    private final SimpleDateFormat mSimpleDateFormat;

    private final List<SwipeLayout> mUnCloseList = new ArrayList<>();

    @SuppressLint("SimpleDateFormat")
    public ReadRecordAdapter() {
        super(R.layout.rv_item_read_record);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void closeAll(SwipeLayout layout) {
        for (SwipeLayout swipeLayout : mUnCloseList) {
            if (layout == swipeLayout) {
                continue;
            }
            if (swipeLayout.getOpenStatus() != SwipeLayout.Status.Open) {
                continue;
            }
            swipeLayout.close();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    closeAll(null);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, ReadRecordModel item) {
        SwipeLayout sl = helper.getView(R.id.sl);
        sl.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                closeAll(layout);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                mUnCloseList.add(layout);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
                mUnCloseList.remove(layout);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
            }
        });
        if (TextUtils.isEmpty(item.getTitle())) {
            helper.setText(R.id.tv_title, item.getLink());
        } else {
            helper.setText(R.id.tv_title, Html.fromHtml(item.getTitle()));
        }
        String time = mSimpleDateFormat.format(new Date(item.getTime()));
        helper.setText(R.id.tv_time, time);
        helper.addOnClickListener(R.id.rl_top, R.id.tv_delete, R.id.tv_open, R.id.tv_copy);
    }
}
