package per.goweii.basic.core.mvp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载Fragment
 *
 * @author Cuizhen
 * @version v1.0.0
 * @date 2018/3/10-下午12:35
 */
public abstract class LazyFragment extends CacheFragment {
    private static final long LOAD_DATA_DELAY = 300;

    private boolean visible = false;
    private boolean firstVisible = false;
    private boolean created = false;
    private View rootView = null;
    private Handler handler = new Handler();
    private Runnable doVisible = new Runnable() {
        @Override
        public void run() {
            onVisible();
        }
    };
    private Runnable doFirstVisible = new Runnable() {
        @Override
        public void run() {
            onVisibleFirst();
        }
    };

    @Override
    public View getRootView() {
        return rootView;
    }

    public boolean isVisibleToUser() {
        return visible;
    }

    public boolean isFirseVisible() {
        return firstVisible;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!created) {
            return;
        }
        if (firstVisible && isVisibleToUser) {
            onFirstVisible();
            firstVisible = false;
        }
        if (isVisibleToUser) {
            onVisibleChange(true);
            visible = true;
            return;
        }
        if (visible) {
            visible = false;
            onVisibleChange(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        created = true;
        onCreateRootView(savedInstanceState);
        return rootView;
    }

    /**
     * 获取布局id
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 重写该方法替代{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * 可以在这里完成P层的绑定操作，与{@link #onDestroyView()}对应
     *
     * @param savedInstanceState Bundle
     */
    protected void onCreateRootView(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        if (created) {
            if (getUserVisibleHint()) {
                if (firstVisible) {
                    onFirstVisible();
                    firstVisible = false;
                }
                onVisibleChange(true);
                visible = true;
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        created = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        initVariable();
        super.onDestroy();
    }

    private void initVariable() {
        firstVisible = true;
        visible = false;
        created = false;
        rootView = null;
    }

    /**
     * 当fragment可见状态发生变化时才回调
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param visible true  不可见 -> 可见
     *                false 可见  -> 不可见
     */
    protected void onVisibleChange(boolean visible) {
        if (visible) {
            handler.postDelayed(doVisible, LOAD_DATA_DELAY);
        } else {
            handler.removeCallbacks(doVisible);
            handler.removeCallbacks(doFirstVisible);
            onInvisible();
        }
    }

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    private void onFirstVisible() {
        handler.postDelayed(doFirstVisible, LOAD_DATA_DELAY);
    }

    protected void onVisibleFirst() {
    }

    protected void onVisible() {
    }

    protected void onInvisible() {
    }
}
