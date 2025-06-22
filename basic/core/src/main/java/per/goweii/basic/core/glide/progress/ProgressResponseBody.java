package per.goweii.basic.core.glide.progress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/9/17
 */
public class ProgressResponseBody extends ResponseBody {

    private final String url;
    private final ResponseBody responseBody;
    private final OnResponseListener listener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(String url, ResponseBody responseBody, @Nullable OnResponseListener listener) {
        this.url = url;
        this.responseBody = responseBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long totalBytesRead = 0;

            @Override
            public Timeout timeout() {
                if (listener != null) {
                    listener.onTimeout(url, totalBytesRead, contentLength());
                }
                return super.timeout();
            }

            @Override
            public void close() throws IOException {
                if (listener != null) {
                    listener.onClose(url, totalBytesRead, contentLength());
                }
                super.close();
            }

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                if (listener != null) {
                    if (bytesRead < 0) {
                        listener.onDone(url, contentLength());
                    } else {
                        if (totalBytesRead == 0) {
                            listener.onStart(url, contentLength());
                        }
                        totalBytesRead += bytesRead;
                        listener.onRead(url, totalBytesRead, contentLength());
                    }
                }
                return bytesRead;
            }
        };
    }

    public interface OnResponseListener {

        void onStart(String url, long totalBytes);

        void onRead(String url, long readBytes, long totalBytes);

        void onDone(String url, long totalBytes);

        void onClose(String url, long readBytes, long totalBytes);

        void onTimeout(String url, long readBytes, long totalBytes);
    }
}
