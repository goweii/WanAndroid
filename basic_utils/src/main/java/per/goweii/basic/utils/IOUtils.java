package per.goweii.basic.utils;

import android.database.Cursor;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author Cuizhen
 * @date 2018/2/10
 * GitHub: https://github.com/goweii
 */
public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * 关闭 IO
     *
     * @param closeables closeables
     */
    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            close(closeable);
        }
    }

    /**
     * 关闭 Cursor
     *
     * @param cursor Cursor
     */
    public static void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * 关闭 Cursor
     *
     * @param cursors cursors
     */
    public static void close(Cursor... cursors) {
        if (cursors == null) {
            return;
        }
        for (Cursor cursor : cursors) {
            close(cursor);
        }
    }

    /**
     * @param in InputStream
     * @return byte[]
     */
    public static byte[] readBytes(InputStream in) {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(out);
        }
    }

    public static byte[] readBytes(InputStream in, long skip, int size) {
        byte[] result = null;
        try {
            if (skip > 0) {
                long skipped;
                while (skip > 0 && (skipped = in.skip(skip)) > 0) {
                    skip -= skipped;
                }
            }
            result = new byte[size];
            for (int i = 0; i < size; i++) {
                result[i] = (byte) in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String readStr(InputStream in) {
        return readStr(in, "UTF-8");
    }

    public static String readStr(InputStream in, String charset) {
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new InputStreamReader(in, charset);
            char[] buf = new char[1024];
            int len;
            while ((len = reader.read(buf)) >= 0) {
                sb.append(buf, 0, len);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(reader);
        }
        return sb.toString();
    }

    public static void writeStr(OutputStream out, String str) {
        writeStr(out, str, "UTF-8");
    }

    public static void writeStr(OutputStream out, String str, String charset) {
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(out, charset);
            writer.write(str);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    public static void copy(InputStream in, OutputStream out) {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        int len;
        byte[] buffer = new byte[1024];
        try {
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
