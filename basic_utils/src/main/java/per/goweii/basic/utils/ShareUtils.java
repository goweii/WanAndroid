package per.goweii.basic.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.File;

import per.goweii.basic.utils.bitmap.BitmapUtils;

/**
 * @author CuiZhen
 * @date 2019/11/10
 * GitHub: https://github.com/goweii
 */
public class ShareUtils {

    public static void shareBitmap(Context context, Bitmap bitmap) {
        File file = BitmapUtils.saveBitmapToCache(bitmap);
        bitmap.recycle();
        if (file == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, UriUtils.getFileUri(file));
        intent = Intent.createChooser(intent, "分享到");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void shareLink(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        intent = Intent.createChooser(intent, "分享到");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
