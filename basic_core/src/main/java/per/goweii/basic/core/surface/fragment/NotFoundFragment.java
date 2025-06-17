package per.goweii.basic.core.surface.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import per.goweii.basic.core.R;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.databinding.BasicCoreFragmentNotFoundBinding;
import per.goweii.basic.core.mvp.MvpPresenter;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/3/29
 */
public class NotFoundFragment extends BaseFragment {

    public static NotFoundFragment create(String msg) {
        NotFoundFragment fragment = new NotFoundFragment();
        Bundle args = new Bundle(1);
        args.putString("msg", msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    protected ViewBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return BasicCoreFragmentNotFoundBinding.inflate(inflater, container, false);
    }

    @Override
    protected MvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        TextView tvMsg = findViewById(R.id.basic_core_tv_msg);
        Bundle args = getArguments();
        if (args != null) {
            String msg = args.getString("msg");
            if (!TextUtils.isEmpty(msg)) {
                tvMsg.setText(msg);
            }
        }
    }

    @Override
    protected void loadData() {
    }
}
