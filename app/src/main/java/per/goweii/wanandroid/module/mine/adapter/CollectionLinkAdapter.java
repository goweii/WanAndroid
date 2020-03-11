package per.goweii.wanandroid.module.mine.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class CollectionLinkAdapter extends BaseQuickAdapter<CollectionLinkBean, BaseViewHolder> {

    private final List<SwipeLayout> mUnCloseList = new ArrayList<>();

    public CollectionLinkAdapter() {
        super(R.layout.rv_item_collection_link);
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
    protected void convert(BaseViewHolder helper, CollectionLinkBean item) {
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
        helper.setText(R.id.tv_title, Html.fromHtml(item.getName()));
        helper.setText(R.id.tv_url, item.getLink());
        helper.addOnClickListener(R.id.rl_top, R.id.tv_delete, R.id.tv_edit, R.id.tv_open, R.id.tv_copy);
    }
}
