package per.goweii.wanandroid.module.knowledge.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import butterknife.BindView;
import per.goweii.actionbarex.ActionBarCommon;
import per.goweii.basic.core.adapter.MultiFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.knowledge.fragment.KnowledgeArticleFragment;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeBean;
import per.goweii.wanandroid.utils.MagicIndicatorUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgeArticleActivity extends BaseActivity {

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.mi)
    MagicIndicator mi;
    @BindView(R.id.vp)
    ViewPager vp;

    public static void start(Context context, KnowledgeBean knowledgeBean, int currPos) {
        Intent intent = new Intent(context, KnowledgeArticleActivity.class);
        intent.putExtra("knowledgeBean", knowledgeBean);
        intent.putExtra("currPos", currPos);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_knowledge_article;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        KnowledgeBean bean = (KnowledgeBean) getIntent().getSerializableExtra("knowledgeBean");
        int currPos = getIntent().getIntExtra("currPos", 0);

        abc.getTitleTextView().setText(bean.getName());

        MultiFragmentPagerAdapter<KnowledgeBean, KnowledgeArticleFragment> adapter =
                new MultiFragmentPagerAdapter<>(
                        getSupportFragmentManager(),
                        new MultiFragmentPagerAdapter.FragmentCreator<KnowledgeBean, KnowledgeArticleFragment>() {
                            @Override
                            public KnowledgeArticleFragment create(KnowledgeBean data) {
                                return KnowledgeArticleFragment.create(data);
                            }

                            @Override
                            public String getTitle(KnowledgeBean data) {
                                return data.getName();
                            }
                        });
        vp.setAdapter(adapter);
        CommonNavigator commonNavigator = MagicIndicatorUtils.commonNavigator(mi, vp, adapter);
        adapter.setDataList(bean.getChildren());
        commonNavigator.notifyDataSetChanged();
        vp.setCurrentItem(currPos);
    }

    @Override
    protected void loadData() {
    }
}
