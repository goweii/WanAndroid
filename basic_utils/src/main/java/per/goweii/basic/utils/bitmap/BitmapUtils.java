package per.goweii.basic.utils.bitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import per.goweii.basic.utils.UriUtils;
import per.goweii.basic.utils.Utils;
import per.goweii.basic.utils.file.CacheUtils;

/**
 * @author CuiZhen
 * @date 2019/11/3
 * GitHub: https://github.com/goweii
 */
public class BitmapUtils {

    /**
     * @param bmp     获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static File saveGallery(Bitmap bmp, String picName) {
        FileOutputStream outStream = null;
        try {
            File gallery = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(gallery, picName + ".jpg");
            outStream = new FileOutputStream(file.getPath());
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = UriUtils.getFileUri(file);
            intent.setData(uri);
            Utils.getAppContext().sendBroadcast(intent);
            return file;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File saveBitmapToCache(Bitmap bm) {
        FileOutputStream outStream = null;
        try {
            String dir = CacheUtils.getCacheDir();
            File f = new File(dir, System.currentTimeMillis() + ".jpg");
            outStream = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
