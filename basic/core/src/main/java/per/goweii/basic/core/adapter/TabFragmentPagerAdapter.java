package per.goweii.basic.core.adapter;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * @author CuiZhen
 * @date 2019/5/11
 * GitHub: https://github.com/goweii
 */
public class TabFragmentPagerAdapter<T> extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private final ViewPager mViewPager;
    private final LinearLayout mTabContainer;
    private final int mTabItemRes;
    private Page<T>[] mPages = null;

    public TabFragmentPagerAdapter(@NonNull FragmentManager fm,
                                   @NonNull ViewPager viewPager,
                                   @NonNull LinearLayout tabContainer,
                                   @LayoutRes int tabItemRes) {
        super(fm);
        mViewPager = viewPager;
        mTabContainer = tabContainer;
        mTabItemRes = tabItemRes;
        mViewPager.setAdapter(this);
        mViewPager.addOnPageChangeListener(this);
    }

    @SafeVarargs
    public final void setPages(Page<T>... pages) {
        mViewPager.setOffscreenPageLimit(pages.length);
        mTabContainer.removeAllViews();
        mPages = pages;
        for (Page<T> page : mPages) {
            initPageTab(page);
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        notifyPageDataChanged();
    }

    public void notifyPageDataChanged() {
        int currPos = mViewPager.getCurrentItem();
        for (int i = 0; i < mPages.length; i++) {
            mPages[i].notifyAdapterBindData(currPos == i);
        }
    }

    private void initPageTab(@NonNull Page<T> page) {
        page.view = LayoutInflater.from(mTabContainer.getContext()).inflate(mTabItemRes, mTabContainer, false);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) page.view.getLayoutParams();
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        params.width = 0;
        params.weight = 1;
        mTabContainer.addView(page.view, params);
        final GestureDetector mGestureDetector = new GestureDetector(page.view.getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                switchCurrentItem(page);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                page.notifyAdapterDoubleTap();
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
        page.view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    private void switchCurrentItem(Page<T> page) {
        for (int i = 0; i < getCount(); i++) {
            Fragment fragment = getItem(i);
            if (page.fragment == fragment) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        return mPages[i].fragment;
    }

    @Override
    public int getCount() {
        return mPages == null ? 0 : mPages.length;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mPages.length; i++) {
            Page<T> page = mPages[i];
            page.notifyAdapterBindData(position == i);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public static class Page<T> {
        @NonNull
        private final Fragment fragment;
        @NonNull
        private final T data;
        @NonNull
        private final TabAdapter<T> adapter;
        private View view;

        public Page(@NonNull Fragment fragment, @NonNull T data, @NonNull TabAdapter<T> adapter) {
            this.fragment = fragment;
            this.data = data;
            this.adapter = adapter;
        }

        @NonNull
        public T getData() {
            return data;
        }

        public void notifyAdapterBindData(boolean selected) {
            adapter.onBindData(view, data, selected);
        }

        public void notifyAdapterDoubleTap() {
            adapter.onDoubleTap(fragment);
        }

        public interface TabAdapter<T> {
            void onBindData(@NonNull View view, @NonNull T data, boolean selected);

            void onDoubleTap(Fragment fragment);
        }
    }
}
