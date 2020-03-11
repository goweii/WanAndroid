package per.goweii.basic.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.IntRange;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户名输入的自定义EditText
 * 最大字符个数限制  中文一个算2个
 * 限制仅可输入中文英文数字和空格，且空格不可以出现在第一位或者在中间位置连续出现
 *
 * @author Cuizhen
 * @date 2018/8/8-下午3:16
 */
public class UserNameEditText extends AppCompatEditText implements TextWatcher {

    private int maxLetterCount = 0;

    public UserNameEditText(Context context) {
        this(context, null);
    }

    public UserNameEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLines(1);
        setFilters(new InputFilter[]{new LengthFilter(), new ChEnIntFilter()});
        addTextChangedListener(this);
    }

    public void setMaxLetterCount(@IntRange(from = 0) int maxLetterCount) {
        this.maxLetterCount = maxLetterCount;
    }

    public String getName() {
        String name = getText().toString();
        if (!TextUtils.isEmpty(name) && name.length() > 0) {
            char last = name.charAt(name.length() - 1);
            if (last == ' ') {
                name = name.substring(0, name.length() - 1);
            }
        }
        return name;
    }

    public boolean matchChEnInt(CharSequence str) {
        String regEx = "[a-zA-Z0-9\u4e00-\u9fa5 ]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public boolean matchSpace(CharSequence str) {
        String regEx = "\\s+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int count = 0;
        if (s != null && s.length() > 0) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }
        }
        if (mOnUserNameLengthChangeListener != null) {
            mOnUserNameLengthChangeListener.onChange(count);
        }
    }

    private class ChEnIntFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (matchChEnInt(source)) {
                if (matchSpace(source)) {
                    if (dstart == 0) {
                        return "";
                    } else {
                        if (dest.charAt(dstart - 1) == ' ') {
                            return "";
                        } else {
                            return " ";
                        }
                    }
                }
                return source;
            }
            return "";
        }
    }

    private class LengthFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
            int dindex = 0;
            int count = 0;

            while (count <= maxLetterCount && dindex < dest.length()) {
                char c = dest.charAt(dindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLetterCount) {
                return dest.subSequence(0, dindex - 1);
            }

            int sindex = 0;
            while (count <= maxLetterCount && sindex < src.length()) {
                char c = src.charAt(sindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLetterCount) {
                sindex--;
            }

            return src.subSequence(0, sindex);
        }
    }

    private OnUserNameLengthChangeListener mOnUserNameLengthChangeListener = null;

    public void setOnUserNameLengthChangeListener(OnUserNameLengthChangeListener onUserNameLengthChangeListener) {
        mOnUserNameLengthChangeListener = onUserNameLengthChangeListener;
    }

    public interface OnUserNameLengthChangeListener {
        void onChange(int length);
    }
}
