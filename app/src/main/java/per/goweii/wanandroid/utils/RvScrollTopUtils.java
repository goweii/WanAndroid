package per.goweii.wanandroid.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @author CuiZhen
 * @date 2019/5/30
 * GitHub: https://github.com/goweii
 */
public class RvScrollTopUtils {

    public static void smoothScrollTop(RecyclerView rv){
        if (rv != null) {
            RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int first = linearLayoutManager.findFirstVisibleItemPosition();
                int last = linearLayoutManager.findLastVisibleItemPosition();
                int visibleCount = last - first + 1;
                int scrollIndex = visibleCount * 2 - 1;
                if (first > scrollIndex) {
                    rv.scrollToPosition(scrollIndex);
                }
            }
            rv.smoothScrollToPosition(0);
        }
    }

}
