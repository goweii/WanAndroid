package per.goweii.basic.utils.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

import per.goweii.basic.utils.Utils;

/**
 * 显示相关帮助类
 * 1.获取屏幕宽度
 * 2.获取屏幕高度
 * 3.获取屏幕密度相关
 * 4.获取状态栏高度
 * 5.dp/px/sp相互转换
 *
 * @author Cuizhen
 * @date 2017/12/28
 * GitHub: https://github.com/goweii
 */
@SuppressLint("StaticFieldLeak")
public final class DisplayInfoUtils {

    private final Context mContext;
    private final DisplayMetrics mDisplayMetrics;

    private static class Holder {
        private static final DisplayInfoUtils INSTANCE = new DisplayInfoUtils();
    }

    private DisplayInfoUtils() {
        mContext = Utils.getAppContext();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
    }

    public static DisplayInfoUtils getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 获取屏幕宽度像素
     *
     * @return px
     */
    public int getWidthPixels() {
        return mDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度像素
     *
     * @return px
     */
    public int getHeightPixels() {
        return mDisplayMetrics.heightPixels;
    }

    /**
     * 获取屏幕像素密度(每英寸多少像素)
     *
     * @return dpi
     */
    public int getDensityDpi() {
        return mDisplayMetrics.densityDpi;
    }

    /**
     * 获取屏幕密度(像素密度/160)
     *
     * @return float
     */
    public float getDensity() {
        return mDisplayMetrics.density;
    }

    /**
     * 字体缩放比例（一般和屏幕密度相等）
     *
     * @return float
     */
    public float getScaledDensity() {
        return mDisplayMetrics.scaledDensity;
    }

    /**
     * X方向的像素密度
     *
     * @return dpi
     */
    public float getXdpi() {
        return mDisplayMetrics.xdpi;
    }

    /**
     * Y方向的像素密度
     *
     * @return dpi
     */
    public float getYdpi() {
        return mDisplayMetrics.ydpi;
    }

    /**
     * 获取状态栏高度像素
     *
     * @return px
     */
    public int getStatusBarHeight() {
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 检测是否有虚拟导航栏
     */
    public boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = mContext.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    /**
     * 获取导航栏高度
     */
    public int getNavigationBarHeight() {
        int rid = mContext.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return mContext.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public Point getAppUsableScreenSize() {
        WindowManager windowManager;
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point(0, 0);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getSize(size);
        }
        return size;
    }

    /**
     * dp: dip，Density-independent pixel(设备独立像素), 是安卓开发用的长度单位，1dp表示在屏幕像素点密度为160ppi时1px长度
     * px: pixel，像素，电子屏幕上组成一幅图画或照片的最基本单元
     * dp转px
     *
     * @param dp dp
     * @return px
     */
    public float dp2px(float dp) {
        return dp * getDensity();
    }

    /**
     * px: pixel，像素，电子屏幕上组成一幅图画或照片的最基本单元
     * dp: dip，Density-independent pixel(设备独立像素), 是安卓开发用的长度单位，1dp表示在屏幕像素点密度为160ppi时1px长度
     * px转dp
     *
     * @param px px
     * @return dp
     */
    public float px2dp(float px) {
        return px / getDensity();
    }

    /**
     * sp: scale-independent pixel，放大的像素，安卓开发用的字体大小单位
     * px: pixel，像素，电子屏幕上组成一幅图画或照片的最基本单元
     * sp转px
     *
     * @param sp sp
     * @return px
     */
    public float sp2px(float sp) {
        return sp * getScaledDensity();
    }

    /**
     * px: pixel，像素，电子屏幕上组成一幅图画或照片的最基本单元
     * sp: scale-independent pixel，放大的像素，安卓开发用的字体大小单位
     * px转sp
     *
     * @param px px
     * @return sp
     */
    public float px2sp(float px) {
        return px / getScaledDensity();
    }

    /**
     * dp: dip，Density-independent pixel(设备独立像素), 是安卓开发用的长度单位，1dp表示在屏幕像素点密度为160ppi时1px长度
     * sp: scale-independent pixel，放大的像素，安卓开发用的字体大小单位
     * dp转sp
     *
     * @param dp dp
     * @return sp
     */
    public float dp2sp(float dp) {
        return dp * getDensity() / getScaledDensity();
    }

    /**
     * sp: scale-independent pixel，放大的像素，安卓开发用的字体大小单位
     * dp: dip，Density-independent pixel(设备独立像素), 是安卓开发用的长度单位，1dp表示在屏幕像素点密度为160ppi时1px长度
     * sp转dp
     *
     * @param sp sp
     * @return dp
     */
    public float sp2dp(float sp) {
        return sp * getScaledDensity() / getDensity();
    }
}
