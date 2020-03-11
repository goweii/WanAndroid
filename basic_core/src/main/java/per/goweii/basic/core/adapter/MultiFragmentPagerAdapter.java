package per.goweii.basic.core.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class MultiFragmentPagerAdapter<E, F extends Fragment> extends FragmentStatePagerAdapter {

    private final FragmentCreator<E, F> mCreator;
    private List<E> mDataList = null;

    public MultiFragmentPagerAdapter(FragmentManager fm, FragmentCreator<E, F> creator) {
        super(fm);
        mCreator = creator;
    }

    public List<E> getDataList() {
        return mDataList;
    }

    public void setDataList(List<E> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mCreator.create(mDataList.get(i), i);
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mCreator.getTitle(mDataList.get(position));
    }

    public interface FragmentCreator<E, F> {
        F create(E data, int pos);
        String getTitle(E data);
    }
}
