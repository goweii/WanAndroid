package per.goweii.basic.core.mvp;

import android.util.SparseArray;
import android.view.View;

import per.goweii.swipeback.SwipeBackActivity;

/**
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/4/4-下午1:23
 */
public abstract class CacheActivity extends SwipeBackActivity {

    private SparseArray<View> mViewCaches = null;

    @Override
    public <T extends View> T findViewById(int id) {
        if (mViewCaches == null) {
            mViewCaches = new SparseArray<>();
        }
        View view = mViewCaches.get(id);
        if (view == null) {
            view = getWindow().getDecorView().findViewById(android.R.id.content).findViewById(id);
            mViewCaches.put(id, view);
        }
        return (T) view;
    }

}
