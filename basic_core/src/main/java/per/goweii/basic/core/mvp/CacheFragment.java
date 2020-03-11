package per.goweii.basic.core.mvp;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

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
