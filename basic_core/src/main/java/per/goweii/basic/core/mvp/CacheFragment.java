package per.goweii.basic.core.mvp;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/4/2
 */
public abstract class CacheFragment extends Fragment {

    private SparseArray<View> mViewCaches = null;

    public abstract View getRootView();

    public <T extends View> T findViewById(@IdRes int id) {
        if (mViewCaches == null) {
            mViewCaches = new SparseArray<>();
        }
        View view = mViewCaches.get(id);
        if (view == null) {
            view = getRootView().findViewById(id);
            mViewCaches.put(id, view);
        }
        return (T) view;
    }
}
