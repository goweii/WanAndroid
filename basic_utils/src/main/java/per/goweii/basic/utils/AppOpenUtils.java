package per.goweii.basic.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author CuiZhen
 * @date 2019/11/3
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AppOpenUtils {
    /**
     * 打开指定的QQ聊天页面
     *
     * @param context 上下文
     * @param qq      QQ号码
     */
    public static boolean openQQChat(Context context, String qq) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打开指定的QQ群聊天页面
     *
     * @param context 上下文
     * @param group   QQ群号码
     */
    public static boolean openQQGroup(Context context, String group) {
        String url = "mqqwpa://im/chat?chat_type=group&uin=" + group;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打开指定的QQ群聊天页面
     *
     * @param context 上下文
     * @param key     由QQ官网生成的Key
     */
    public static boolean joinQQGroup(Context context, String key) {
        String url = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key;
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean openWechat(Context context) {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(AppInfoUtils.PackageName.WECHAT,
                AppInfoUtils.PackageName.WECHAT + ".ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
