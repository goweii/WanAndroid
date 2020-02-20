package per.goweii.wanandroid.utils.wanpwd;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.wanandroid.BuildConfig;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.cdkey.CDKeyUtils;

/**
 * @author CuiZhen
 * @date 2020/1/1
 * GitHub: https://github.com/goweii
 */
public class CreateCDKeyWanPwd implements IWanPwd {

    private Runnable mRunnable;

    public CreateCDKeyWanPwd(String content) {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!UserUtils.getInstance().isLogin()) {
                    ToastMaker.showShort("请登录后使用该功能");
                    return;
                }
                int id = UserUtils.getInstance().getUserId();
                if (!TextUtils.equals(String.valueOf(id), BuildConfig.DEVELOPER_ID)) {
                    ToastMaker.showShort("该功能仅限开发者账号使用");
                    return;
                }
                String cdkey = CDKeyUtils.createCDKey(content);
                StringBuilder s = new StringBuilder();
                s.append("【玩口令】这是一个激活码口令，仅限特定账号使用，户制泽条消息");
                s.append(String.format(BuildConfig.WANPWD_FORMAT, BuildConfig.WANPWD_TYPE_CDKEY, cdkey));
                s.append("打開最美玩安卓客户端激活");
                CopyUtils.copyText(s.toString());
                ToastMaker.showShort("口令已复制");
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
        return "###激活码生成###\n\n！！！警告！！！\n\n该功能仅限开发者使用";
    }

    @Override
    public String getBtnText() {
        return "复制";
    }
}
