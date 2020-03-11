package per.goweii.basic.utils;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/4/4
 */
public class EditTextUtils {

    public static void changeCursorColor(TextView target, int colorInt){
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(target);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(target);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[1];
            drawables[0] = target.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(colorInt, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static void setEditTextMaxLength(@NonNull EditText editText, @IntRange(from = 1) int length){
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    public static void setEditTextDigitsKey(@NonNull EditText editText, @NonNull String keys){
        editText.setKeyListener(DigitsKeyListener.getInstance(keys));
    }

    /**
     * 输入框划分分组
     * 如银行卡号，邀请码等
     *
     * @param editText   输入框
     * @param groupCount 每个分组的字符个数
     * @param separator  分割符号
     */
    public static void divideEditTextWithGroup(@NonNull EditText editText, @IntRange(from = 1) int groupCount, char separator) {
        editText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;
            int cursorLocation = 0;
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int separatorNum = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                separatorNum = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == separator) {
                        separatorNum++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    cursorLocation = editText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == separator) {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }
                    index = 0;
                    int separatorNum = 0;
                    while (index < buffer.length()) {
                        if ((index + 1) / (groupCount + 1) == 0) {
                            buffer.insert(index, separator);
                            separatorNum++;
                        }
                        index++;
                    }
                    if (separatorNum > this.separatorNum) {
                        cursorLocation += (separatorNum - this.separatorNum);
                    }
                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (cursorLocation > str.length()) {
                        cursorLocation = str.length();
                    } else if (cursorLocation < 0) {
                        cursorLocation = 0;
                    }
                    editText.setText(str);
                    Editable editable = editText.getText();
                    Selection.setSelection(editable, cursorLocation);
                    isChanged = false;
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public static void makeEditTextWithShowInputWordsNumber(EditText inputEditText, TextView numTextView, int maxNum) {
        numTextView.setText(inputEditText.getText().toString().length() + "/" + maxNum);
        inputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxNum)});
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                numTextView.setText(editable.toString().length() + "/" + maxNum);
            }
        });
    }


    public static void makeEditTextWithAmountInput(final EditText editText, float minMoney, float allMoney) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                    if (".".equals(s.toString().trim())) {
                        s = "0" + s;
                        editText.setText(s);
                        editText.setSelection(2);
                    }
                    if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                        if (!".".equals(s.toString().substring(1, 2))) {
                            editText.setText(s.subSequence(0, 1));
                            editText.setSelection(1);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!"".equals(s.toString()) && !".".equals(s.toString())) {
                    if (!"".equals(s.toString()) && !".".equals(s.toString())) {
                        if (Float.valueOf(s.toString()) > allMoney) {
                            setTextWithSelection(editText, String.valueOf(allMoney));
                        }
                    }
                }
            }
        });
    }

    public static void setTextWithSelection(EditText editText, CharSequence text) {
        editText.setText(text);
        editText.setSelection(editText.getText().toString().length());
    }

    public static void bindEditTextAndSubmitViewWithNotEmpty(final View submitView, final EditText... editTexts) {
        if (editTexts == null || editTexts.length == 0) {
            return;
        }
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean allInputted = true;
                    for (EditText et : editTexts) {
                        final String text = et.getText().toString().trim();
                        final boolean empty = TextUtils.isEmpty(text);
                        if (empty) {
                            allInputted = false;
                            break;
                        }
                    }
                    if (allInputted) {
                        submitView.setEnabled(true);
                        submitView.setAlpha(1);
                    } else {
                        submitView.setEnabled(false);
                        submitView.setAlpha(0.6f);
                    }
                }
            });
        }
    }
}
