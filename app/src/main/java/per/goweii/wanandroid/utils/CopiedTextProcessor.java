package per.goweii.wanandroid.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import per.goweii.basic.utils.LogUtils;
import per.goweii.basic.utils.Utils;
import per.goweii.wanandroid.utils.wanpwd.WanPwdParser;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class CopiedTextProcessor {

    private final ClipboardManager mClipboardManager;

    private ProcessCallback mProcessCallback = null;

    private String mLastCopiedText = null;
    private String mCurrProcessText = null;

    private static class Holder {
        private static CopiedTextProcessor sInstance = new CopiedTextProcessor();
    }

    private CopiedTextProcessor() {
        mClipboardManager = (ClipboardManager) Utils.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static CopiedTextProcessor getInstance() {
        return Holder.sInstance;
    }

    public void processed() {
        if (TextUtils.isEmpty(mCurrProcessText)) {
            return;
        }
        mLastCopiedText = mCurrProcessText;
    }

    public void setProcessCallback(ProcessCallback processCallback) {
        mProcessCallback = processCallback;
    }

    public void process() {
        mCurrProcessText = getPrimaryClipText();
        if (TextUtils.isEmpty(mCurrProcessText)) return;
        if (!isDiff(mCurrProcessText)) return;
        if (isLink(mCurrProcessText)) {
            if (mProcessCallback != null) {
                mProcessCallback.isLink(mCurrProcessText);
            }
            return;
        }
        WanPwdParser parser = WanPwdParser.match(mCurrProcessText);
        if (parser != null) {
            if (mProcessCallback != null) {
                mProcessCallback.isPassword(parser);
            }
            return;
        }
        LogUtils.d("CopiedTextProcessor", "nothing");
    }

    @NonNull
    private String getPrimaryClipText() {
        ClipData clip = mClipboardManager.getPrimaryClip();
        if (clip == null || clip.getItemCount() <= 0) {
            return "";
        }
        ClipData.Item item = clip.getItemAt(0);
        if (TextUtils.isEmpty(item.getText())) {
            return "";
        }
        return item.getText().toString();
    }

    private boolean isDiff(String text) {
        return !TextUtils.equals(mLastCopiedText, text);
    }

    private boolean isLink(String text) {
        try {
            Uri uri = Uri.parse(text);
            return TextUtils.equals(uri.getScheme(), "http") || TextUtils.equals(uri.getScheme(), "https");
        } catch (Exception e) {
            return false;
        }
    }

    public interface ProcessCallback {
        void isLink(String link);

        void isPassword(WanPwdParser pwd);
    }

}
