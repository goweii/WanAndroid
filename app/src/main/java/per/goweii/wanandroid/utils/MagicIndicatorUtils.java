package per.goweii.wanandroid.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import per.goweii.basic.core.adapter.MultiFragmentPagerAdapter;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class MagicIndicatorUtils {

    public static <E, F extends Fragment> CommonNavigator commonNavigator(
            MagicIndicator mi, ViewPager vp,
            MultiFragmentPagerAdapter<E, F> adapter) {
        CommonNavigator navigator = new CommonNavigator(vp.getContext());
        final int padding16 = (int) DisplayInfoUtils.getInstance().dp2px(16);
        navigator.setLeftPadding(padding16);
        navigator.setRightPadding(padding16);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return adapter.getCount();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(adapter.getPageTitle(index));
                simplePagerTitleView.setSelectedColor(ResUtils.getColor(R.color.text_white));
                simplePagerTitleView.setNormalColor(ResUtils.getColor(R.color.text_white_alpha));
                int paddingH = (int) DisplayInfoUtils.getInstance().dp2px(15);
                int paddingV = (int) DisplayInfoUtils.getInstance().dp2px(5);
                simplePagerTitleView.setPadding(paddingH, paddingV, paddingH, paddingV);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vp.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(ResUtils.getColor(R.color.black_alpha));
                indicator.setVerticalPadding((int) DisplayInfoUtils.getInstance().dp2px(5));
                indicator.setHorizontalPadding((int) DisplayInfoUtils.getInstance().dp2px(15));
                return indicator;
            }
        });
        mi.setNavigator(navigator);
        ViewPagerHelper.bind(mi, vp);
        return navigator;
    }
}
