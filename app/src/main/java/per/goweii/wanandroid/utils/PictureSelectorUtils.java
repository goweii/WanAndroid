package per.goweii.wanandroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/6/4
 * GitHub: https://github.com/goweii
 */
public class PictureSelectorUtils {

    public static void ofImage(Fragment fragment, int requestCode) {
        PictureSelector.create(fragment)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.PictureSelectorStyle)
                .selectionMode(PictureConfig.SINGLE)
                .enableCrop(true)
                .withAspectRatio(1, 1)
                .isCamera(false)
                .forResult(requestCode);
    }

    public static String forResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // 图片、视频、音频选择结果回调
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList != null && selectList.size() > 0) {
                return selectList.get(0).getPath();
            }
        }
        return null;
    }

}
