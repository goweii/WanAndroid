package per.goweii.basic.core.glide.progress;

import android.support.annotation.WorkerThread;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/9/17
 */
public interface OnProgressListener {
    @WorkerThread
    void onProgress(float progress);
}
