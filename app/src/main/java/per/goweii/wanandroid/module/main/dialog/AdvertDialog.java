package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.Objects;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.main.model.AdvertBean;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.router.Router;

public class AdvertDialog extends DialogLayer {
    private AdvertBean advertBean;
    private ValueAnimator closeAnimator = null;

    public AdvertDialog(@NonNull Context context) {
        super(context);
        backgroundDimDefault();
        cancelableOnClickKeyBack(false);
        cancelableOnTouchOutside(false);
        contentView(R.layout.dialog_advert);
        onClickToDismiss(R.id.dialog_advert_iv_close);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View view) {
                Router.routeTo(advertBean.getRoute());
            }
        }, R.id.dialog_advert_iv_image);
    }

    public AdvertDialog setAdvertBean(AdvertBean advertBean) {
        this.advertBean = advertBean;
        return this;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        ImageView iv_image = getView(R.id.dialog_advert_iv_image);
        ImageLoader.image(Objects.requireNonNull(iv_image), advertBean.getImage());
        MaterialProgressBar pb_close = getView(R.id.dialog_advert_pb_close);
        Objects.requireNonNull(pb_close);
        if (advertBean.getDuration() > 0) {
            pb_close.setVisibility(View.VISIBLE);
            pb_close.setMax(advertBean.getDuration());
            pb_close.setProgress(advertBean.getDuration());
        } else {
            pb_close.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (closeAnimator != null) {
            closeAnimator.cancel();
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        if (advertBean.getDuration() > 0) {
            startCloseAnimator();
        }
    }

    private void startCloseAnimator() {
        closeAnimator = ValueAnimator.ofInt(advertBean.getDuration(), 0);
        closeAnimator.setInterpolator(new LinearInterpolator());
        closeAnimator.setDuration(advertBean.getDuration());
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MaterialProgressBar pb_close = getView(R.id.dialog_advert_pb_close);
                Objects.requireNonNull(pb_close);
                pb_close.setMax(advertBean.getDuration());
                pb_close.setProgress((int) animation.getAnimatedValue());
            }
        });
        closeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        closeAnimator.start();
    }
}
