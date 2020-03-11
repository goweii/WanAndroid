package per.goweii.basic.core.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

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
 * GitHub: https://github.com/goweii
 */
public class PermissionUtils {

    public static final class PermissionGroup {
        static final String[] CALENDAR = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        };
        static final String[] CAMERA = new String[]{
                Manifest.permission.CAMERA
        };
        static final String[] CONTACTS = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.GET_ACCOUNTS
        };
        static final String[] LOCATION = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        static final String[] MICROPHONE = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        static final String[] PHONE;
        static final String[] SENSORS;
        static final String[] SMS = new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.RECEIVE_MMS
        };
        static final String[] STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        static {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PHONE = new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_CALL_LOG,
                        Manifest.permission.ADD_VOICEMAIL,
                        Manifest.permission.USE_SIP,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.READ_PHONE_NUMBERS,
                        Manifest.permission.ANSWER_PHONE_CALLS};
            } else {
                PHONE = new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_CALL_LOG,
                        Manifest.permission.ADD_VOICEMAIL,
                        Manifest.permission.USE_SIP,
                        Manifest.permission.PROCESS_OUTGOING_CALLS};
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                SENSORS = new String[]{Manifest.permission.BODY_SENSORS};
            } else {
                SENSORS = new String[0];
            }
        }
    }

    private static PermissionDialog.GroupType getGroupType(String permission) {
        if (contain(PermissionGroup.CALENDAR, permission)) {
            return PermissionDialog.GroupType.CALENDAR;
        }
        if (contain(PermissionGroup.CAMERA, permission)) {
            return PermissionDialog.GroupType.CAMERA;
        }
        if (contain(PermissionGroup.CONTACTS, permission)) {
            return PermissionDialog.GroupType.CONTACTS;
        }
        if (contain(PermissionGroup.LOCATION, permission)) {
            return PermissionDialog.GroupType.LOCATION;
        }
        if (contain(PermissionGroup.MICROPHONE, permission)) {
            return PermissionDialog.GroupType.MICROPHONE;
        }
        if (contain(PermissionGroup.PHONE, permission)) {
            return PermissionDialog.GroupType.PHONE;
        }
        if (contain(PermissionGroup.SENSORS, permission)) {
            return PermissionDialog.GroupType.SENSORS;
        }
        if (contain(PermissionGroup.SMS, permission)) {
            return PermissionDialog.GroupType.SMS;
        }
        if (contain(PermissionGroup.STORAGE, permission)) {
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
