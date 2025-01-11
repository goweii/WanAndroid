package per.goweii.wanandroid.module.main.dialog;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Constant;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.utils.GuideSPUtils;
import per.goweii.wanandroid.utils.UrlOpenUtils;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * GitHub: https://github.com/goweii
 */
public class PrivacyPolicyDialog extends DialogLayer {

    public static void showIfFirst(Context context, CompleteCallback callback) {
        if (GuideSPUtils.getInstance().isPrivacyPolicyShown()) {
            if (callback != null) {
                callback.onComplete();
            }
            return;
        }
        new PrivacyPolicyDialog(context, callback).show();
    }

    private PrivacyPolicyDialog(Context context, CompleteCallback callback) {
        super(context);
        addOnDismissListener(new OnDismissListener() {
            @Override
            public void onPreDismiss(@NonNull Layer layer) {
            }

            @Override
            public void onPostDismiss(@NonNull Layer layer) {
                if (callback != null) {
                    callback.onComplete();
                }
            }
        });
        setContentView(R.layout.dialog_privacy_policy);
        setBackgroundBlurRadius(4);
        setBackgroundBlurSimple(20);
        setCancelableOnClickKeyBack(false);
        setCancelableOnTouchOutside(false);
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                GuideSPUtils.getInstance().setPrivacyPolicyShown();
            }
        }, R.id.dialog_privacy_policy_tv_yes);
        addOnClickToDismissListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                WanApp.exitApp();
            }
        }, R.id.dialog_privacy_policy_tv_no);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        TextView content = requireView(R.id.dialog_privacy_policy_tv_content);
        String text = getActivity().getString(R.string.privacy_policy_content);
        String link = getActivity().getString(R.string.privacy_policy_content_link);
        int start = text.indexOf(link);
        int end = start + link.length();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new Clickable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlOpenUtils.Companion
                        .with(Constant.PRIVACY_POLICY_URL)
                        .open(getActivity());
            }
        }), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(new SpannableStringBuilder().append(spannableString));
        content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        private Clickable(View.OnClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(@NonNull View v) {
            mListener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ResUtils.getThemeColor(getActivity(), R.attr.colorMain));
            ds.setUnderlineText(false);
        }
    }

    public interface CompleteCallback {
        void onComplete();
    }
}
