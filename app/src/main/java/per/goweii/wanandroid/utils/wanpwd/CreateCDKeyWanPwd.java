package per.goweii.wanandroid.utils.wanpwd;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.cdkey.CDKeyUtils;

/**
 * @author CuiZhen
 * @date 2020/1/1
 * GitHub: https://github.com/goweii
 */
public class CreateCDKeyWanPwd implements IWanPwd {

    private final Runnable mRunnable;

    public CreateCDKeyWanPwd(String content) {
        mRunnable = new Runnable() {
            @SuppressWarnings("StringBufferReplaceableByString")
            @Override
            public void run() {
                if (!UserUtils.getInstance().isLogin()) {
                    ToastMaker.showShort(ResUtils.getString(R.string.please_sign_in_to_use_this_feature));
                    return;
                }
                int id = UserUtils.getInstance().getWanId();
                if (!TextUtils.equals(String.valueOf(id), BuildConfig.DEVELOPER_ID)) {
                    ToastMaker.showShort(ResUtils.getString(R.string.this_feature_is_only_available_to_developer_accounts));
                    return;
                }
                String cdkey = CDKeyUtils.getInstance().create(content);
                StringBuilder s = new StringBuilder();
                s.append(ResUtils.getString(R.string.this_is_an_activation_code_passcode));
                s.append(String.format(BuildConfig.WANPWD_FORMAT, BuildConfig.WANPWD_TYPE_CDKEY, cdkey));
                s.append(ResUtils.getString(R.string.open_the_most_beautiful_android_client_to_activate));
                CopyUtils.copyText(s.toString());
                ToastMaker.showShort(ResUtils.getString(R.string.copied));
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
        return ResUtils.getString(R.string.activation_code_generation_warning);
    }

    @Override
    public String getBtnText() {
        return ResUtils.getString(R.string.copy);
    }
}
