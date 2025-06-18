package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.cdkey.CDKeyUtils;

/**
 * @author CuiZhen
 * @date 2020/1/1
 * GitHub: https://github.com/goweii
 */
public class CDKeyWanPwd implements IWanPwd {

    private final Runnable mRunnable;

    public CDKeyWanPwd(String content) {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!UserUtils.getInstance().isLogin()) {
                    ToastMaker.showShort(ResUtils.getString(R.string.please_sign_in_first));
                    return;
                }
                int id = UserUtils.getInstance().getWanId();
                if (CDKeyUtils.getInstance().check(String.valueOf(id), content)) {
                    CDKeyUtils.getInstance().set(content);
                    ToastMaker.showShort(ResUtils.getString(R.string.the_activation_was_successful));
                } else {
                    ToastMaker.showShort(ResUtils.getString(R.string.the_activation_code_is_invalid));
                }
            }
        };
    }

    @Nullable
    @Override
    public Runnable getRunnable() {
        return mRunnable;
    }

    @Override
    public String getShowText() {
        return ResUtils.getString(R.string.you_ve_found_an_activation_code);
    }

    @Override
    public String getBtnText() {
        return ResUtils.getString(R.string.activate);
    }
}
