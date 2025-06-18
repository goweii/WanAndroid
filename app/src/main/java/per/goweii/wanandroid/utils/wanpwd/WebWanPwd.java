package per.goweii.wanandroid.utils.wanpwd;

import androidx.annotation.Nullable;

import java.net.URLDecoder;

import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.router.Router;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class WebWanPwd implements IWanPwd {

    private final String content;
    private String mShowText;
    private String mBtnText;
    private Runnable mRunnable;

    public WebWanPwd(String content) {
        this.content = content;
        parse();
    }

    @Nullable
    @Override
    public Runnable getRunnable() {
        return mRunnable;
    }

    @Override
    public String getShowText() {
        return mShowText;
    }

    @Override
    public String getBtnText() {
        return mBtnText;
    }

    private void parse() {
        mShowText = ResUtils.getString(R.string.you_ve_found_a_link_to_a_web_page);
        mBtnText = ResUtils.getString(R.string.open);
        final String url = URLDecoder.decode(content);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Router.routeTo(url);
            }
        };
    }
}
