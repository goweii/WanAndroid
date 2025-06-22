package per.goweii.basic.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串正则匹配
 *
 * @author Cuizhen
 * @date 2018/7/18-下午1:52
 */
public class RegexUtils {

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 18;

    private static final String REGEX_E_MAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    private static final String REGEX_PHONE = "^(1[3456789][0-9])\\d{8}";
    private static final String REGEX_PASSWORD_NUN_AND_EN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
    private static final String REGEX_PASSWORD_NUN_OR_EN = "^[a-z0-9A-Z]{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
    private static final String REGEX_ID_NUM = "^\\d{15}|\\d{18}|\\d{17}(\\d|X|x)";
    private static final String REGEX_PASSWORD_LENGTH = "^.{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
    private static final String REGEX_PHONE_LENGTH = "^.{11}$";
    private static final String REGEX_PHONE_HIDE = "(\\d{3})\\d{4}(\\d{4})";
    private static final String REGEX_E_MAIL_HIDE = "(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)";

    /**
     * 邮箱格式是否正确
     *
     * @param email String
     * @return boolean
     */
    public static boolean matchEmail(String email) {
        return match(email, REGEX_E_MAIL);
    }

    /**
     * 手机号格式是否正确
     *
     * @param phone String
     * @return boolean
     */
    public static boolean matchPhone(String phone) {
        return match(phone, REGEX_PHONE);
    }

    /**
     * 手机号长度是否正确
     *
     * @param phone String
     * @return boolean
     */
    public static boolean matchPhoneLength(String phone) {
        return match(phone, REGEX_PHONE_LENGTH);
    }

    /**
     * 手机号用****号隐藏中间数字
     *
     * @param phone String
     * @return String
     */
    public static String hidePhone(String phone) {
        return phone.replaceAll(REGEX_PHONE_HIDE, "$1****$2");
    }

    /**
     * 邮箱用****号隐藏前面的字母
     *
     * @param email String
     * @return String
     */
    public static String hideEmail(String email) {
        return email.replaceAll(REGEX_E_MAIL_HIDE, "$1****$3$4");
    }

    /**
     * 密码格式是否正确
     *
     * @param psw String
     * @return boolean
     */
    public static boolean matchPassword(String psw) {
        return match(psw, REGEX_PASSWORD_NUN_OR_EN);
    }

    /**
     * 密码长度是否正确
     *
     * @param psw String
     * @return boolean
     */
    public static boolean matchPasswordLength(String psw) {
        return match(psw, REGEX_PASSWORD_LENGTH);
    }

    /**
     * 身份证号格式是否正确
     *
     * @param id String
     * @return boolean
     */
    public static boolean matchIdNum(String id) {
        return match(id, REGEX_ID_NUM);
    }

    /**
     * 字符串正则匹配
     *
     * @param s     待匹配字符串
     * @param regex 正则表达式
     * @return boolean
     */
    public static boolean match(String s, String regex) {
        if (s == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}
