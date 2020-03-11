package per.goweii.basic.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.IntRange;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * 自定义EditText
 * 最大字符个数限制  中文一个算2个
 * 限制仅可输入中文英文数字和空格，且空格不可以出现在第一位或者在中间位置连续出现
 *
 * @author Cuizhen
 * @date 2018/8/8-下午3:16
 */
public class SignatureEditText extends AppCompatEditText implements TextWatcher {

    private int maxLetterCount = 0;

    public SignatureEditText(Context context) {
        this(context, null);
    }

    public SignatureEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLines(1);
        setFilters(new InputFilter[]{new LengthFilter()});
        addTextChangedListener(this);
    }

    public void setMaxLetterCount(@IntRange(from = 0) int maxLetterCount) {
        this.maxLetterCount = maxLetterCount;
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
        if (mOnInputLengthChangeListener != null) {
            mOnInputLengthChangeListener.onChange(count);
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

    private OnInputLengthChangeListener mOnInputLengthChangeListener = null;

    public void setOnInputLengthChangeListener(OnInputLengthChangeListener onInputLengthChangeListener) {
        mOnInputLengthChangeListener = onInputLengthChangeListener;
    }

    public interface OnInputLengthChangeListener {
        void onChange(int length);
    }
}
