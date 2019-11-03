package per.goweii.basic.utils.bitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import per.goweii.basic.utils.Utils;

/**
 * @author CuiZhen
 * @date 2019/11/3
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class BitmapUtils {

    /**
     * @param bmp     获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static boolean saveGallery(Bitmap bmp, String picName) {
        File file;
        FileOutputStream outStream = null;
        try {
            File gallery = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            file = new File(gallery, picName + ".jpg");
            outStream = new FileOutputStream(file.getPath());
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        } catch (Exception e) {
            e.getStackTrace();
            file = null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file == null) {
            return false;
        }
        MediaStore.Images.Media.insertImage(Utils.getAppContext().getContentResolver(), bmp, file.getPath(), null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        Utils.getAppContext().sendBroadcast(intent);
        return true;
    }
}
