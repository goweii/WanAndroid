package per.goweii.wanandroid.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.core.glide.transformation.BlurTransformation;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class ImageLoader {

    public static void image(ImageView imageView, String url){
        GlideHelper.with(imageView.getContext())
                .errorHolder(R.drawable.image_holder)
                .placeHolder(R.drawable.image_holder)
                .cache(true)
                .load(url)
                .into(imageView);
    }

    public static void gif(ImageView imageView, String url) {
        GlideHelper.with(imageView.getContext())
                .asGif()
                .errorHolder(R.drawable.image_holder)
                .placeHolder(R.drawable.image_holder)
                .cache(true)
                .load(url)
                .into(imageView);
    }

    public static void banner(ImageView imageView, String url){
        GlideHelper.with(imageView.getContext())
                .errorHolder(R.drawable.image_holder)
                .placeHolder(R.drawable.image_holder)
                .cache(true)
                .load(url)
                .into(imageView);
    }

    public static void userIcon(ImageView imageView, String url){
        GlideHelper.with(imageView.getContext())
                .cache(true)
                .load(url)
                .into(imageView);
    }

    public static void userIcon(ImageView imageView, Uri uri) {
        GlideHelper.with(imageView.getContext())
                .cache(true)
                .load(uri)
                .into(imageView);
    }

    public static void userBlur(ImageView imageView, String url){
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.color.transparent);
            return;
        }
        GlideHelper.with(imageView.getContext())
                .cache(true)
                .load(url)
                .transformation(new BlurTransformation(0.2F))
                .into(imageView);
    }

    public static void userBlur(ImageView imageView, Uri uri) {
        if (uri == null) {
            imageView.setImageResource(R.color.transparent);
            return;
        }
        GlideHelper.with(imageView.getContext())
                .cache(true)
                .load(uri)
                .transformation(new BlurTransformation(0.2F))
                .into(imageView);
    }

    public static void userBlur(ImageView imageView, int res){
        GlideHelper.with(imageView.getContext())
//                .errorHolder(R.drawable.image_holder)
//                .placeHolder(R.drawable.image_holder)
                .cache(true)
                .load(res)
                .transformation(new BlurTransformation(0.2F))
                .into(imageView);
    }
}
