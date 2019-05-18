package per.goweii.wanandroid.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.adapter.FixedFragmentPagerAdapter;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.fragment.HomeFragment;
import per.goweii.wanandroid.module.knowledge.fragment.KnowledgeFragment;
import per.goweii.wanandroid.module.mine.fragment.MineFragment;
import per.goweii.wanandroid.module.project.fragment.ProjectFragment;
import per.goweii.wanandroid.module.wxarticle.fragment.WxFragment;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.iv_bb_home)
    ImageView iv_bb_home;
    @BindView(R.id.tv_bb_home)
    TextView tv_bb_home;
    @BindView(R.id.iv_bb_knowledge)
    ImageView iv_bb_knowledge;
    @BindView(R.id.tv_bb_knowledge)
    TextView tv_bb_knowledge;
    @BindView(R.id.iv_bb_wechat)
    ImageView iv_bb_wechat;
    @BindView(R.id.tv_bb_wechat)
    TextView tv_bb_wechat;
    @BindView(R.id.iv_bb_project)
    ImageView iv_bb_project;
    @BindView(R.id.tv_bb_project)
    TextView tv_bb_project;
    @BindView(R.id.iv_bb_mine)
    ImageView iv_bb_mine;
    @BindView(R.id.tv_bb_mine)
    TextView tv_bb_mine;

    private FixedFragmentPagerAdapter mPagerAdapter;

    private long lastClickTime = 0L;
    private int lastClickPos = 0;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        vp.addOnPageChangeListener(this);
        vp.setOffscreenPageLimit(4);
        mPagerAdapter = new FixedFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mPagerAdapter);
    }

    @Override
    protected void loadData() {
        mPagerAdapter.setFragmentList(
                HomeFragment.create(),
                KnowledgeFragment.create(),
                WxFragment.create(),
                ProjectFragment.create(),
                MineFragment.create()
        );
        vp.setCurrentItem(0);
        onPageSelected(vp.getCurrentItem());
    }

    @OnClick({
            R.id.ll_bb_home, R.id.ll_bb_knowledge, R.id.ll_bb_wechat, R.id.ll_bb_project, R.id.ll_bb_mine
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected boolean onClick1(View v) {
        switch (v.getId()) {
            default:
                return false;
            case R.id.ll_bb_home:
                vp.setCurrentItem(0);
                break;
            case R.id.ll_bb_knowledge:
                vp.setCurrentItem(1);
                break;
            case R.id.ll_bb_wechat:
                vp.setCurrentItem(2);
                break;
            case R.id.ll_bb_project:
                vp.setCurrentItem(3);
                break;
            case R.id.ll_bb_mine:
                vp.setCurrentItem(4);
                break;
        }
        notifyScrollTop(vp.getCurrentItem());
        return true;
    }

    private void notifyScrollTop(int pos) {
        long currClickTime = System.currentTimeMillis();
        if (lastClickPos == pos && currClickTime - lastClickTime <= 500) {
            Fragment fragment = mPagerAdapter.getItem(pos);
            if (fragment instanceof ScrollTop) {
                ScrollTop scrollTop = (ScrollTop) fragment;
                scrollTop.scrollTop();
            }
        }
        lastClickPos = pos;
        lastClickTime = currClickTime;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        iv_bb_home.setImageResource(R.drawable.ic_home_normal);
        iv_bb_knowledge.setImageResource(R.drawable.ic_book_normal);
        iv_bb_wechat.setImageResource(R.drawable.ic_wechat_normal);
        iv_bb_project.setImageResource(R.drawable.ic_project_normal);
        iv_bb_mine.setImageResource(R.drawable.ic_mine_normal);
        switch (i) {
            default:
                break;
            case 0:
                iv_bb_home.setImageResource(R.drawable.ic_home_selected);
                break;
            case 1:
                iv_bb_knowledge.setImageResource(R.drawable.ic_book_selected);
                break;
            case 2:
                iv_bb_wechat.setImageResource(R.drawable.ic_wechat_selected);
                break;
            case 3:
                iv_bb_project.setImageResource(R.drawable.ic_project_selected);
                break;
            case 4:
                iv_bb_mine.setImageResource(R.drawable.ic_mine_selected);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public interface ScrollTop{
        void scrollTop();
    }
}
