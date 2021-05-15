package per.goweii.wanandroid.module.main.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.anylayer.widget.SwipeLayout;
import per.goweii.basic.utils.InputMethodUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.CommentItemEntity;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public class CommentInputDialog extends DialogLayer {

    private EditText et_input;
    private TextView tv_input;
    private TextView tv_reply;

    private boolean isPreview = false;

    private final CommentItemEntity mReply;
    private final String mText;

    private final SimpleCallback<String> mOnInputText;
    private final SimpleListener mOnPublish;

    public CommentInputDialog(Context context,
                              String text,
                              CommentItemEntity reply,
                              SimpleCallback<String> onInputText,
                              SimpleListener onPublish
    ) {
        super(context);
        mText = text;
        mReply = reply;
        mOnInputText = onInputText;
        mOnPublish = onPublish;
        contentView(R.layout.dialog_comment_input);
        swipeDismiss(SwipeLayout.Direction.BOTTOM);
        backgroundDimDefault();
        cancelableOnClickKeyBack(true);
        cancelableOnTouchOutside(true);
        gravity(Gravity.BOTTOM);
        onClick(new Layer.OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
            }
        }, R.id.dialog_comment_input_tv_preview);
        onClickToDismiss(new Layer.OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                if (mOnPublish != null) {
                    mOnPublish.onResult();
                }
            }
        }, R.id.dialog_comment_input_tv_publish);
        compatSoftInput(true, R.id.dialog_comment_input_et_input);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAttach() {
        super.onAttach();
        et_input = requireView(R.id.dialog_comment_input_et_input);
        tv_input = requireView(R.id.dialog_comment_input_tv_input);
        tv_reply = requireView(R.id.dialog_comment_input_tv_reply);

        isPreview = false;
        et_input.setVisibility(View.VISIBLE);
        tv_input.setVisibility(View.GONE);

        if (mReply != null && mReply.getComment() != null) {
            if (mReply.getComment().getUser() != null) {
                tv_reply.setText("@" + mReply.getComment().getUser().getUsername());
            } else {
                tv_reply.setText("@已删除");
            }
        } else {
            tv_reply.setText("");
        }
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mOnInputText != null) {
                    mOnInputText.onResult(et_input.getText().toString());
                }
            }
        });
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!et_input.isFocused()) {
                    InputMethodUtils.hide(et_input);
                }
            }
        };
        et_input.setOnFocusChangeListener(listener);
        et_input.setText(mText);
    }

    @Override
    protected void onAppear() {
        super.onAppear();
        et_input.setFocusable(true);
        et_input.setFocusableInTouchMode(true);
        et_input.requestFocus();
    }

    @Override
    public void onShow() {
        super.onShow();
        InputMethodUtils.show(et_input);
    }

    @Override
    protected void onDisappear() {
        super.onDisappear();
        InputMethodUtils.hide(et_input);
    }

}
