package per.goweii.basic.utils.bitmap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileDescriptor;
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

    @Nullable
    public static Uri getImageContentUri(Context context, String path) {
        try (Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (new File(path).exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, path);
                    return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }
    }

    // 通过uri加载图片
    @Nullable
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            if (parcelFileDescriptor == null) {
                return null;
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Bitmap getBitmapFromPath(Context context, String path) {
        Uri uri = getImageContentUri(context, path);
        if (uri == null) {
            return null;
        }
        return getBitmapFromUri(context, uri);
    }
}
