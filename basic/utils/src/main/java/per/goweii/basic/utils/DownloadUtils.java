package per.goweii.basic.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 版本更新下载apk
 *
 * @author mac
 * @date 18/5/17
 */
public class DownloadUtils {

    private DownloadUtils(String url, DownloadListener listener) {
        new DownloadApk(listener).execute(url);
    }

    public static DownloadUtils download(String url, DownloadListener listener) {
        return new DownloadUtils(url, listener);
    }

    public static void installApk(Context context, String filePath) {
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        installApk(context, file);
    }

    public static void installApk(Context context, File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, Utils.getAppContext().getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    public interface DownloadListener {
        void onPreExecute();

        void onDownloadLength(int length);

        void onProgressUpdate(int progress);

        void onPostExecute(File apk);
    }

    /**
     * 通过路径去下载
     **/
    static class DownloadApk extends AsyncTask<String, Integer, File> {

        private final DownloadListener downloadListener;

        DownloadApk(DownloadListener listener) {
            this.downloadListener = listener;
        }

        @Override
        protected File doInBackground(String... params) {
            File file = null;
            try {
                URL url = new URL(params[0]);
                try {
                    final String fileName = Utils.getAppContext().getPackageName() + ".apk";
                    String parentPath;
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    } else {
                        parentPath = Environment.getDownloadCacheDirectory().getAbsolutePath();
                    }
                    File tmpFile = new File(parentPath);
                    if (!tmpFile.exists()) {
                        tmpFile.mkdirs();
                    }
                    file = new File(parentPath, fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    int length = conn.getContentLength();
                    downloadListener.onDownloadLength(length);
                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buf = new byte[256];
                    conn.connect();
                    double count = 0;
                    if (conn.getResponseCode() >= 400) {
                    } else {
                        double cou = 0;
                        while (count <= 100) {
                            if (is != null) {
                                int numRead = is.read(buf);
                                cou += numRead;
                                int pro = (int) ((cou / length) * 100);
                                if (numRead <= 0) {
                                    break;
                                } else {
                                    publishProgress(pro);
                                    fos.write(buf, 0, numRead);
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    conn.disconnect();
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            downloadListener.onPostExecute(file);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadListener.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            downloadListener.onProgressUpdate(values[0]);
        }
    }
}
