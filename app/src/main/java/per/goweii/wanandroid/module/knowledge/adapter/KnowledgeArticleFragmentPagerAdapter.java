package per.goweii.wanandroid.module.knowledge.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class KnowledgeArticleFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = null;

    public KnowledgeArticleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }
}
