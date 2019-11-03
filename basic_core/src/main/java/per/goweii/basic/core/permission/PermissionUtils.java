package per.goweii.basic.core.permission;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.runtime.Permission;

import java.util.Arrays;

import per.goweii.anypermission.AnyPermission;
import per.goweii.anypermission.RequestInterceptor;
import per.goweii.anypermission.RequestListener;
import per.goweii.anypermission.RuntimeRequester;
import per.goweii.basic.ui.dialog.PermissionDialog;
import per.goweii.basic.utils.listener.SimpleCallback;

/**
 * @author CuiZhen
 * @date 2019/11/3
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class PermissionUtils {

    private static PermissionDialog.GroupType getGroupType(String permission) {
        if (contain(Permission.Group.CALENDAR, permission)) {
            return PermissionDialog.GroupType.CALENDAR;
        }
        if (contain(Permission.Group.CAMERA, permission)) {
            return PermissionDialog.GroupType.CAMERA;
        }
        if (contain(Permission.Group.CONTACTS, permission)) {
            return PermissionDialog.GroupType.CONTACTS;
        }
        if (contain(Permission.Group.LOCATION, permission)) {
            return PermissionDialog.GroupType.LOCATION;
        }
        if (contain(Permission.Group.MICROPHONE, permission)) {
            return PermissionDialog.GroupType.MICROPHONE;
        }
        if (contain(Permission.Group.PHONE, permission)) {
            return PermissionDialog.GroupType.PHONE;
        }
        if (contain(Permission.Group.SENSORS, permission)) {
            return PermissionDialog.GroupType.SENSORS;
        }
        if (contain(Permission.Group.SMS, permission)) {
            return PermissionDialog.GroupType.SMS;
        }
        if (contain(Permission.Group.STORAGE, permission)) {
            return PermissionDialog.GroupType.STORAGE;
        }
        return null;
    }

    private static boolean contain(String[] group, String permission) {
        return Arrays.asList(group).contains(permission);
    }

    public static RuntimeRequester request(RequestListener listener, Context context, int code, String... permissions) {
        return AnyPermission.with(context)
                .runtime(code)
                .permissions(permissions)
                .onBeforeRequest(new RequestInterceptor<String>() {
                    @Override
                    public void intercept(@NonNull String data, @NonNull RequestInterceptor.Executor executor) {
                        PermissionDialog.GroupType groupType = getGroupType(data);
                        PermissionDialog.with(context)
                                .setGoSetting(false)
                                .setGroupType(groupType)
                                .setOnNextListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.execute();
                                    }
                                })
                                .setOnCloseListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .onBeenDenied(new RequestInterceptor<String>() {
                    @Override
                    public void intercept(@NonNull String data, @NonNull Executor executor) {
                        PermissionDialog.GroupType groupType = getGroupType(data);
                        PermissionDialog.with(context)
                                .setGoSetting(false)
                                .setGroupType(groupType)
                                .setOnNextListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.execute();
                                    }
                                })
                                .setOnCloseListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .onGoSetting(new RequestInterceptor<String>() {
                    @Override
                    public void intercept(@NonNull String data, @NonNull Executor executor) {
                        PermissionDialog.GroupType groupType = getGroupType(data);
                        PermissionDialog.with(context)
                                .setGoSetting(true)
                                .setGroupType(groupType)
                                .setOnNextListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.execute();
                                    }
                                })
                                .setOnCloseListener(new SimpleCallback<Void>() {
                                    @Override
                                    public void onResult(Void data) {
                                        executor.cancel();
                                    }
                                })
                                .show();
                    }
                })
                .request(listener);
    }
}
