package per.goweii.basic.utils;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class RandomUtils {

    public static String randomLetter(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int r = (int) (Math.random() * 26);
            char c = Math.random() > 0.5 ? 'a' : 'A';
            s.append((char) (c + r));
        }
        return s.toString();
    }
}
