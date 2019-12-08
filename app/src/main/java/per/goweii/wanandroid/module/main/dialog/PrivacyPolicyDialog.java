package per.goweii.wanandroid.module.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.WanApp;
import per.goweii.wanandroid.module.main.activity.WebActivity;
import per.goweii.wanandroid.utils.GuideSPUtils;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class PrivacyPolicyDialog extends DialogLayer {

    public static void showIfFirst(Context context) {
        if (GuideSPUtils.getInstance().isPrivacyPolicyShown()) {
            return;
        }
        new PrivacyPolicyDialog(context).show();
    }

    private PrivacyPolicyDialog(Context context) {
        super(context);
        contentView(R.layout.dialog_privacy_policy)
                .backgroundDimDefault()
                .cancelableOnClickKeyBack(false)
                .cancelableOnTouchOutside(false)
                .onClickToDismiss(new OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        GuideSPUtils.getInstance().setPrivacyPolicyShown();
                    }
                }, R.id.dialog_privacy_policy_tv_yes)
                .onClickToDismiss(new OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        WanApp.exitApp();
                    }
                }, R.id.dialog_privacy_policy_tv_no);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        TextView content = getView(R.id.dialog_privacy_policy_tv_content);
        String text = getActivity().getString(R.string.privacy_policy_content);
        String link = getActivity().getString(R.string.privacy_policy_content_link);
        int start = text.indexOf(link);
        int end = start + link.length();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new Clickable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.start(getActivity(), "file:///android_asset/privacy_policy.html");
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
            ds.setColor(getActivity().getResources().getColor(R.color.main));
            ds.setUnderlineText(false);
        }
    }
}
