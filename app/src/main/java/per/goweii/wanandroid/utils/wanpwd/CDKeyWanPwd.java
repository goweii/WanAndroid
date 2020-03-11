package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.wanandroid.utils.UserUtils;
import per.goweii.wanandroid.utils.cdkey.CDKeyUtils;

/**
 * @author CuiZhen
 * @date 2020/1/1
 * GitHub: https://github.com/goweii
 */
public class CDKeyWanPwd implements IWanPwd {

    private Runnable mRunnable;

    public CDKeyWanPwd(String content) {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!UserUtils.getInstance().isLogin()) {
                    ToastMaker.showShort("请先登录");
                    return;
                }
                int id = UserUtils.getInstance().getUserId();
                if (CDKeyUtils.isActiveCDKey(String.valueOf(id), content)) {
                    CDKeyUtils.getInstance().setActivatedCDKey(content);
                    ToastMaker.showShort("激活成功，重启APP后生效");
                } else {
                    ToastMaker.showShort("激活码无效");
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
        return "你发现了一个激活码！\n激活码仅与当前登录账户绑定，更换设备或账户后需重新激活，成功激活后将会去除所有广告，是否立即激活？";
    }

    @Override
    public String getBtnText() {
        return "激活";
    }
}
