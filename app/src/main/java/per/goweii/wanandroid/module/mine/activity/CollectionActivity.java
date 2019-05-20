package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarEx;
import per.goweii.actionbarex.IconView;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.mine.fragment.CollectionArticleFragment;
import per.goweii.wanandroid.module.mine.fragment.CollectionLinkFragment;
import per.goweii.wanandroid.utils.MagicIndicatorUtils;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionActivity extends BaseActivity {

    @BindView(R.id.ab)
    ActionBarEx ab;
    @BindView(R.id.vp)
    ViewPager vp;

    public static void start(Context context) {
        Intent intent = new Intent(context, CollectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        IconView ivBack = ab.getView(R.id.action_bar_fixed_magic_indicator_iv_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FixedFragmentPagerAdapter adapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        adapter.setTitles("文章", "网址");
        adapter.setFragmentList(
                CollectionArticleFragment.create(),
                CollectionLinkFragment.create()
        );
        vp.setAdapter(adapter);
        MagicIndicatorUtils.commonNavigator(ab.getView(R.id.mi), vp, adapter);
    }

    @Override
    protected void loadData() {
    }
}
