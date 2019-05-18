package per.goweii.basic.core;

import android.app.Activity;

import per.goweii.basic.utils.listener.SimpleCallback;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/4/27
 */
public class CoreInit {

    private SimpleCallback<Activity> mOnGoLoginCallback = null;

    private static class Holder{
        private static final CoreInit INSTANCE = new CoreInit();
    }

    private CoreInit(){
    }

    public static CoreInit getInstance(){
        return Holder.INSTANCE;
    }

    public void setOnGoLoginCallback(SimpleCallback<Activity> callback){
        mOnGoLoginCallback = callback;
    }

    public SimpleCallback<Activity> getOnGoLoginCallback() {
        return mOnGoLoginCallback;
    }
}
