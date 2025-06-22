package per.goweii.basic.core.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import per.goweii.basic.utils.Utils;
import per.goweii.basic.utils.listener.SimpleCallback;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class LoginReceiver extends BroadcastReceiver {

    public static final String ACTION = LoginReceiver.class.getName();

    public static final String TYPE = "TYPE";

    public static final int UNKNOW = 0;
    public static final int NOT_LOGIN = 1;

    private LocalBroadcastManager mLocalBroadcastManager;
    private SimpleCallback<Integer> mCallback;

    public static void send(int type) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(Utils.getAppContext());
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra(TYPE, NOT_LOGIN);
        manager.sendBroadcast(intent);
    }

    public static void sendNotLogin() {
        send(NOT_LOGIN);
    }

    public static LoginReceiver register(Activity activity, SimpleCallback<Integer> callback) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        LoginReceiver loginReceiver = new LoginReceiver(localBroadcastManager, callback);
        localBroadcastManager.registerReceiver(loginReceiver, filter);
        return loginReceiver;
    }

    public void unregister() {
        if (mLocalBroadcastManager != null) {
            mLocalBroadcastManager.unregisterReceiver(this);
        }
        mLocalBroadcastManager = null;
        mCallback = null;
    }

    public LoginReceiver(LocalBroadcastManager localBroadcastManager, SimpleCallback<Integer> callback) {
        mLocalBroadcastManager = localBroadcastManager;
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mCallback != null) {
            mCallback.onResult(intent.getIntExtra(TYPE, UNKNOW));
        }
    }
}
