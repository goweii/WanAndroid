package per.goweii.basic.core.surface.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import per.goweii.basic.core.R;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.databinding.BasicCoreActivitySingleFragBinding;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.basic.core.surface.fragment.NotFoundFragment;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/4/9
 */
public class SingleFragActivity extends BaseActivity {

    private Fragment mFragment = null;

    public static void start(Context context, Class<Fragment> fragmentClass) {
        Intent intent = new Intent(context, SingleFragActivity.class);
        intent.putExtra("fragmentClassName", fragmentClass.getName());
        context.startActivity(intent);
    }

    @Nullable
    @Override
    protected ViewBinding initViewBinding(@NonNull LayoutInflater inflater) {
        return BasicCoreActivitySingleFragBinding.inflate(inflater);
    }

    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        String fragmentClassName = getIntent().getStringExtra("fragmentClassName");
        try {
            Class<Fragment> clazz = (Class<Fragment>) Class.forName(fragmentClassName);
            mFragment = clazz.newInstance();
        } catch (Exception ignore) {
        }
        if (mFragment == null) {
            mFragment = NotFoundFragment.create(fragmentClassName);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.basic_core_fl_fragment, mFragment);
        ft.commit();
    }

    @Override
    protected void loadData() {
    }
}
