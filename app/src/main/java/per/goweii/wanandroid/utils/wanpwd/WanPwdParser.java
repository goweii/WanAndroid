package per.goweii.wanandroid.utils.wanpwd;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import per.goweii.basic.utils.LogUtils;
import per.goweii.wanandroid.BuildConfig;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class WanPwdParser {

    private static final Pattern PATTERN_PASSWORD = Pattern.compile(BuildConfig.WANPWD_PATTERN);

    private final Pwd mPwd;
    private final IWanPwd mWanPwd;

    private WanPwdParser(Pwd pwd) {
        this.mPwd = pwd;
        switch (mPwd.type) {
            default:
            case UNKNOWN:
                mWanPwd = new UnknownWanPwd();
                break;
            case CDKEY:
                mWanPwd = new CDKeyWanPwd(mPwd.content);
                break;
            case QQ:
                mWanPwd = new QQWanPwd(mPwd.content);
                break;
            case FESTIVAL:
                mWanPwd = new FestivalWanPwd(mPwd.content);
                break;
            case USERPAGE:
                mWanPwd = new UserPageWanPwd(mPwd.content);
                break;
            case WEB:
                mWanPwd = new WebWanPwd(mPwd.content);
                break;
            case ABOUTME:
                mWanPwd = new AboutMeWanPwd();
                break;
            case CREATE_CDKEY:
                mWanPwd = new CreateCDKeyWanPwd(mPwd.content);
                break;
        }
    }

    @NonNull
    public IWanPwd getWanPwd() {
        return mWanPwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WanPwdParser parser = (WanPwdParser) o;
        return Objects.equals(mPwd, parser.mPwd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPwd);
    }

    public static WanPwdParser match(String text) {
        List<String> ps = findPasswords(text);
        if (ps == null || ps.size() != 1) {
            return null;
        }
        String pw = ps.get(0);
        return new WanPwdParser(parsePassword(pw));
    }

    private static List<String> findPasswords(String text) {
        Matcher m = PATTERN_PASSWORD.matcher(text);
        List<String> strs = new ArrayList<>();
        while (m.find()) {
            strs.add(m.group());
        }
        return strs;
    }

    private static WanPwdParser.Pwd parsePassword(String pwd) {
        if (!PATTERN_PASSWORD.matcher(pwd).matches()) {
            return new WanPwdParser.Pwd();
        }
        String split = String.format(BuildConfig.WANPWD_FORMAT, "", "").substring(1, 2);
        String[] strs = pwd.substring(1, pwd.length() - 1).split(split);
        for (String str : strs) {
            LogUtils.d("CopiedTextProcessor", "parsePassword=" + str);
        }
        if (strs.length != 2) {
            return new WanPwdParser.Pwd();
        }
        String type = strs[0];
        String content = strs[1];
        return new WanPwdParser.Pwd(type, content);
    }

    public static class Pwd {
        public final WanPwdParser.Type type;
        public final String content;

        Pwd() {
            this(null, null);
        }

        Pwd(String type, String content) {
            this.type = WanPwdParser.Type.find(type);
            this.content = content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pwd pwd = (Pwd) o;
            return type == pwd.type && TextUtils.equals(content, pwd.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, content);
        }
    }

    public enum Type {
        UNKNOWN(""),
        QQ(BuildConfig.WANPWD_TYPE_QQ),
        FESTIVAL(BuildConfig.WANPWD_TYPE_FESTIVAL),
        USERPAGE(BuildConfig.WANPWD_TYPE_USERPAGE),
        CDKEY(BuildConfig.WANPWD_TYPE_CDKEY),
        WEB(BuildConfig.WANPWD_TYPE_WEB),
        ABOUTME(BuildConfig.WANPWD_TYPE_ABOUTME),
        CREATE_CDKEY(BuildConfig.WANPWD_TYPE_CREATE_CDKEY);

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public static Type find(String type) {
            for (Type t : Type.values()) {
                if (TextUtils.equals(t.type, type)) {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }
}
