package per.goweii.basic.core.utils;

import android.view.View;
import android.view.ViewGroup;

import per.goweii.actionbarex.ActionBarEx;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class LoadingBarManager {

    private boolean mHasTryToFindActionBarEx = false;
    private ActionBarEx mActionBarEx = null;
    private View mRootView = null;

    private int count = 0;

    private LoadingBarManager(View rootView) {
        mRootView = rootView;
    }

    public static LoadingBarManager attach(View rootView){
        return new LoadingBarManager(rootView);
    }

    public void detach(){
        mRootView = null;
        mActionBarEx = null;
    }

    public void show(){
        tryToFindActionBarEx();
        if (mActionBarEx != null) {
            count = count < 0 ? 0 : count;
            if (count == 0){
                mActionBarEx.getForegroundLayer().setVisibility(View.VISIBLE);
            }
            count++;
        }
    }

    public void dismiss(){
        tryToFindActionBarEx();
        if (mActionBarEx != null) {
            count--;
            count = count < 0 ? 0 : count;
            if (count == 0){
                mActionBarEx.getForegroundLayer().setVisibility(View.GONE);
            }
        }
    }

    public void clear(){
        tryToFindActionBarEx();
        if (mActionBarEx != null) {
            mActionBarEx.getForegroundLayer().setVisibility(View.GONE);
            count = 0;
        }
    }

    private void tryToFindActionBarEx(){
        if (!mHasTryToFindActionBarEx) {
            mHasTryToFindActionBarEx = true;
            mActionBarEx = findActionBarEx(mRootView);
        }
    }

    private ActionBarEx findActionBarEx(View view){
        if (view instanceof ActionBarEx) {
            return (ActionBarEx) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                ActionBarEx actionBarEx = findActionBarEx(child);
                if (actionBarEx != null) {
                    return actionBarEx;
                }
            }
        }
        return null;
    }
}
