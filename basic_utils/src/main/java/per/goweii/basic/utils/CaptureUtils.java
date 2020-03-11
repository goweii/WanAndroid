package per.goweii.basic.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;

/**
 * @author CuiZhen
 * @date 2019/11/3
 * GitHub: https://github.com/goweii
 */
public class CaptureUtils {

    public static Bitmap captureWebView(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return captureWebViewLollipop(webView);
        } else {
            return captureWebViewKitKat(webView);
        }
    }

    private static Bitmap captureWebViewKitKat(WebView webView) {
        Picture picture = webView.capturePicture();
        int width = picture.getWidth();
        int height = picture.getHeight();
        if (width > 0 && height > 0) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            return bitmap;
        }
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap captureWebViewLollipop(WebView webView) {
        float scale = webView.getScale();
        int width = webView.getWidth();
        int height = (int) (webView.getContentHeight() * scale + 0.5);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
        return bitmap;
    }
}
