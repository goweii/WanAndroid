package per.goweii.wanandroid.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.basic.utils.AppOpenUtils;
import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.Utils;
import per.goweii.wanandroid.BuildConfig;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WanPwdParser {

    private static final Pattern PATTERN_PASSWORD = Pattern.compile(BuildConfig.WANPWD_PATTERN);
    private static final Pattern PATTERN_PASSWORD_PART = Pattern.compile(BuildConfig.WANPWD_PATTERN_PART);

    private final Pwd mPwd;

    private String mContentText;
    private String mBtnText;
    private Runnable mRunnable;

    private WanPwdParser(Pwd pwd) {
        this.mPwd = pwd;
        parse();
    }

    private void parse() {
        mRunnable = null;
        mContentText = "";
        mBtnText = "";
        switch (mPwd.type) {
            case UNKNOW:
            case FESTIVAL:
            case CDKEY:
                mContentText = "嗯！？我怎么没见过种口令？可能是新版本加的吧，快去设置中更新版本再试试吧！";
                mBtnText = "去更新";
                break;
            case QQ:
                mContentText = "你发现了一个QQ号码：" + getQQ() + "，是否立即启动QQ？";
                mBtnText = "启动QQ";
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (AppInfoUtils.isAppInstalled(Utils.getAppContext(), AppInfoUtils.PackageName.QQ)) {
                            AppOpenUtils.openQQChat(Utils.getAppContext(), getQQ());
                        }
                    }
                };
                break;
        }
    }

    public String getContentText() {
        return mContentText;
    }

    public String getBtnText() {
        return mBtnText;
    }

    @Nullable
    public Runnable getRunnable() {
        return mRunnable;
    }

    private String getQQ() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < mPwd.content.length(); i++) {
            char c = mPwd.content.charAt(i);
            if (c >= '0' && c <= '9') {
                s.append(c);
            }
        }
        return s.toString();
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

    private static WanPwdParser.Pwd parsePassword(String pw) {
        if (!PATTERN_PASSWORD.matcher(pw).matches()) {
            return new WanPwdParser.Pwd();
        }
        Matcher m = PATTERN_PASSWORD_PART.matcher(pw);
        List<String> strs = new ArrayList<>();
        while (m.find()) {
            strs.add(m.group());
        }
        for (String str : strs) {
            LogUtils.d("CopiedTextProcessor", "parsePassword=" + str);
        }
        if (strs.size() != 2) {
            return new WanPwdParser.Pwd();
        }
        String type = strs.get(0);
        String content = strs.get(1);
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
        UNKNOW(""),
        QQ(BuildConfig.WANPWD_TYPE_QQ),
        FESTIVAL(BuildConfig.WANPWD_TYPE_FESTIVAL),
        CDKEY(BuildConfig.WANPWD_TYPE_CDKEY);

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
            return UNKNOW;
        }
    }
}
