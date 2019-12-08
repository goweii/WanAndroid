package per.goweii.wanandroid.module.main.dialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.utils.GuideSPUtils;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class WebGuideDialog extends DialogLayer {
    public WebGuideDialog(Context context) {
        super(context);
        contentView(R.layout.dialog_web_guide)
                .backgroundDimDefault()
                .cancelableOnClickKeyBack(false)
                .cancelableOnTouchOutside(false)
                .animStyle(AnimStyle.ALPHA)
                .onClick(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        layer.dismiss();
                        GuideSPUtils.getInstance().setWebGuideShown();
                    }
                }, R.id.dialog_web_guide_tv_know);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        ImageView iv_1 = getView(R.id.dialog_web_guide_iv_1);
        ImageView iv_2 = getView(R.id.dialog_web_guide_iv_2);
        ValueAnimator animator1 = ValueAnimator.ofFloat(1, 1.5F);
        animator1.setDuration(3000L);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                iv_1.setScaleX(value);
                iv_1.setScaleY(value);
            }
        });
        animator1.start();
        ValueAnimator animator2 = ValueAnimator.ofFloat(1, 1.5F);
        animator2.setStartDelay(500L);
        animator2.setDuration(3000L);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.REVERSE);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                iv_2.setScaleX(value);
                iv_2.setScaleY(value);
            }
        });
        animator2.start();
    }

}
