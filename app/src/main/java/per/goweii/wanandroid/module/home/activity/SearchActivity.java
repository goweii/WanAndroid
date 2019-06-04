package per.goweii.wanandroid.module.home.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.utils.EditTextUtils;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.fragment.SearchHistoryFragment;
import per.goweii.wanandroid.module.home.fragment.SearchResultFragment;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.abs)
    ActionBarSearch abs;
    @BindView(R.id.fl)
    FrameLayout fl;

    private SearchHistoryFragment mSearchHistoryFragment;
    private SearchResultFragment mSearchResultFragment;
    private FragmentManager mFm;

    private boolean mIsResultPage = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Nullable
    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        abs.setOnLeftIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsResultPage) {
                    showHistoryFragment();
                } else {
                    finish();
                }
            }
        });
        abs.setOnRightTextClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                String key = abs.getEditTextView().getText().toString();
                search(key);
            }
        });
        mFm = getSupportFragmentManager();
        Fragment searchHistoryFragment = mFm.findFragmentByTag(SearchHistoryFragment.class.getName());
        if (searchHistoryFragment == null) {
            mSearchHistoryFragment = SearchHistoryFragment.create();
        } else {
            mSearchHistoryFragment = (SearchHistoryFragment) searchHistoryFragment;
        }
        Fragment searchResultFragment = mFm.findFragmentByTag(SearchResultFragment.class.getName());
        if (searchResultFragment == null) {
            mSearchResultFragment = SearchResultFragment.create();
        } else {
            mSearchResultFragment = (SearchResultFragment) searchResultFragment;
        }
        FragmentTransaction t = mFm.beginTransaction();
        t.add(R.id.fl, mSearchHistoryFragment, SearchHistoryFragment.class.getName());
        t.add(R.id.fl, mSearchResultFragment, SearchResultFragment.class.getName());
        t.show(mSearchHistoryFragment);
        t.hide(mSearchResultFragment);
        t.commit();
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onBackPressed() {
        if (mIsResultPage) {
            showHistoryFragment();
        } else {
            super.onBackPressed();
        }
    }

    public void search(String key) {
        InputMethodUtils.hide(abs.getEditTextView());
        abs.getEditTextView().clearFocus();
        if (TextUtils.isEmpty(key)) {
            if (mIsResultPage) {
                showHistoryFragment();
            }
        } else {
            EditTextUtils.setTextWithSelection(abs.getEditTextView(), key);
            if (!mIsResultPage) {
                showResultFragment();
            }
            mSearchHistoryFragment.addHistory(key);
            mSearchResultFragment.search(key);
        }
    }

    private void showHistoryFragment() {
        mIsResultPage = false;
        FragmentTransaction t = mFm.beginTransaction();
        t.hide(mSearchResultFragment);
        t.show(mSearchHistoryFragment);
        t.commit();
    }

    private void showResultFragment() {
        mIsResultPage = true;
        FragmentTransaction t = mFm.beginTransaction();
        t.hide(mSearchHistoryFragment);
        t.show(mSearchResultFragment);
        t.commit();
    }
}
