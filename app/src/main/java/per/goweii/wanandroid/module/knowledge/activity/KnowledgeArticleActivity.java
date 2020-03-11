package per.goweii.wanandroid.module.knowledge.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.basic.core.adapter.MultiFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.event.ScrollTopEvent;
import per.goweii.wanandroid.module.knowledge.fragment.KnowledgeArticleFragment;
import per.goweii.wanandroid.module.main.model.ChapterBean;
import per.goweii.wanandroid.utils.MagicIndicatorUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class KnowledgeArticleActivity extends BaseActivity {

    @BindView(R.id.abc)
    ActionBarCommon abc;
    @BindView(R.id.mi)
    MagicIndicator mi;
    @BindView(R.id.vp)
    ViewPager vp;

    private long lastClickTime = 0L;
    private int lastClickPos = 0;

    public static void start(Context context, ChapterBean chapterBean, int currPos) {
        Intent intent = new Intent(context, KnowledgeArticleActivity.class);
        intent.putExtra("chapterBean", chapterBean);
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
        ChapterBean bean = (ChapterBean) getIntent().getSerializableExtra("chapterBean");
        int currPos = getIntent().getIntExtra("currPos", 0);

        abc.getTitleTextView().setText(bean.getName());
        abc.getTitleTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyScrollTop(vp.getCurrentItem());
            }
        });

        MultiFragmentPagerAdapter<ChapterBean, KnowledgeArticleFragment> adapter =
                new MultiFragmentPagerAdapter<>(
                        getSupportFragmentManager(),
                        new MultiFragmentPagerAdapter.FragmentCreator<ChapterBean, KnowledgeArticleFragment>() {
                            @Override
                            public KnowledgeArticleFragment create(ChapterBean data, int pos) {
                                return KnowledgeArticleFragment.create(data, pos);
                            }

                            @Override
                            public String getTitle(ChapterBean data) {
                                return data.getName();
                            }
                        });
        vp.setAdapter(adapter);
        CommonNavigator commonNavigator = MagicIndicatorUtils.commonNavigator(mi, vp, adapter, new SimpleCallback<Integer>() {
            @Override
            public void onResult(Integer data) {
                notifyScrollTop(data);
            }
        });
        adapter.setDataList(bean.getChildren());
        commonNavigator.notifyDataSetChanged();
        vp.setCurrentItem(currPos);
    }

    @Override
    protected void loadData() {
    }

    private void notifyScrollTop(int pos) {
        long currClickTime = System.currentTimeMillis();
        if (lastClickPos == pos && currClickTime - lastClickTime <= Config.SCROLL_TOP_DOUBLE_CLICK_DELAY) {
            new ScrollTopEvent(KnowledgeArticleFragment.class, vp.getCurrentItem()).post();
        }
        lastClickPos = pos;
        lastClickTime = currClickTime;
    }
}
