package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.anylayer.utils.AnimatorHelper;
import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.display.DisplayInfoUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.home.model.ImageBean;
import per.goweii.wanandroid.module.main.model.AdvertBean;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.router.Router;
import per.goweii.wanandroid.widget.ParallaxStackLayout;

public class AdvertDialog extends DialogLayer implements Layer.AnimatorCreator {
    private AdvertBean advertBean;
    private ValueAnimator closeAnimator = null;

    private Integer mActivityRequestedOrientation = null;

    public AdvertDialog(@NonNull Context context) {
        super(context);
        setBackgroundDimDefault();
        setCancelableOnClickKeyBack(false);
        setCancelableOnTouchOutside(false);
        setContentView(R.layout.dialog_advert);
        setContentAnimator(this);
        addOnClickToDismissListener(R.id.dialog_advert_iv_close);
        addOnClickListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                if (TextUtils.isEmpty(advertBean.getRoute())) {
                    return;
                }
                Router.routeTo(advertBean.getRoute());
                dismiss();
            }
        }, R.id.dialog_advert_psl);
        mActivityRequestedOrientation = getActivity().getRequestedOrientation();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public AdvertDialog setAdvertBean(AdvertBean advertBean) {
        this.advertBean = advertBean;
        return this;
    }

    @Nullable
    @Override
    public Animator createInAnimator(@NonNull View target) {
        View psl = requireView(R.id.dialog_advert_psl);
        View rl_close = requireView(R.id.dialog_advert_rl_close);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                AnimatorHelper.createAlphaInAnim(psl),
                AnimatorHelper.createZoomAlphaInAnim(rl_close)
        );
        return animatorSet;
    }

    @Nullable
    @Override
    public Animator createOutAnimator(@NonNull View target) {
        View psl = requireView(R.id.dialog_advert_psl);
        View rl_close = requireView(R.id.dialog_advert_rl_close);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                AnimatorHelper.createAlphaOutAnim(psl),
                AnimatorHelper.createZoomAlphaOutAnim(rl_close)
        );
        return animatorSet;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        MaterialProgressBar pb_close = requireView(R.id.dialog_advert_pb_close);
        if (advertBean.getDuration() > 0) {
            pb_close.setVisibility(View.VISIBLE);
            pb_close.setMax(advertBean.getDuration());
            pb_close.setProgress(advertBean.getDuration());
        } else {
            pb_close.setVisibility(View.INVISIBLE);
        }
        ParallaxStackLayout psl = requireView(R.id.dialog_advert_psl);
        psl.removeAllViews();
        if (advertBean.getImages() == null || advertBean.getImages().isEmpty()) {
            fillBySingleImage(psl, advertBean.getImage());
        } else {
            fillByMultiImages(psl, advertBean.getImages());
        }
    }

    @Override
    protected void onPostShow() {
        super.onPostShow();
        if (advertBean.getDuration() > 0) {
            startCloseAnimator();
        }
    }

    @Override
    protected void onPreDismiss() {
        super.onPreDismiss();
        if (closeAnimator != null) {
            closeAnimator.cancel();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mActivityRequestedOrientation != null) {
            getActivity().setRequestedOrientation(mActivityRequestedOrientation);
        }
    }

    private void fillBySingleImage(ParallaxStackLayout psl, String url) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) psl.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.dialog_advert_rl_close);
        ImageView imageView = new ImageView(getActivity());
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int p32 = (int) ResUtils.getDimens(R.dimen.margin_middle);
        int p16 = (int) ResUtils.getDimens(R.dimen.margin_def);
        imageView.setPadding(p32, p32, p32, p16);
        ImageLoader.roundImage(imageView, url, (int) ResUtils.getDimens(R.dimen.round_radius));
        psl.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @SuppressLint("RtlHardcoded")
    private void fillByMultiImages(ParallaxStackLayout psl, List<ImageBean> images) {
        for (ImageBean imageBean : images) {
            ImageView imageView = new ImageView(getActivity());
            ParallaxStackLayout.LayoutParams layoutParams = new ParallaxStackLayout.LayoutParams(0, 0);
            if (imageBean.getWidth() == ParallaxStackLayout.LayoutParams.MATCH_PARENT) {
                layoutParams.width = ParallaxStackLayout.LayoutParams.MATCH_PARENT;
            } else if (imageBean.getWidth() == ParallaxStackLayout.LayoutParams.WRAP_CONTENT) {
                layoutParams.width = ParallaxStackLayout.LayoutParams.WRAP_CONTENT;
            } else {
                layoutParams.width = (int) (0.5F + DisplayInfoUtils.getInstance().dp2px(imageBean.getWidth()));
            }
            if (imageBean.getHeight() == ParallaxStackLayout.LayoutParams.MATCH_PARENT) {
                layoutParams.height = ParallaxStackLayout.LayoutParams.MATCH_PARENT;
            } else if (imageBean.getHeight() == ParallaxStackLayout.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ParallaxStackLayout.LayoutParams.WRAP_CONTENT;
            } else {
                layoutParams.height = (int) (0.5F + DisplayInfoUtils.getInstance().dp2px(imageBean.getHeight()));
            }
            if ((layoutParams.width == ParallaxStackLayout.LayoutParams.WRAP_CONTENT &&
                    layoutParams.height != ParallaxStackLayout.LayoutParams.WRAP_CONTENT) ||
                    (layoutParams.height == ParallaxStackLayout.LayoutParams.WRAP_CONTENT &&
                            layoutParams.width != ParallaxStackLayout.LayoutParams.WRAP_CONTENT)) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setAdjustViewBounds(true);
            } else if (layoutParams.width == ParallaxStackLayout.LayoutParams.MATCH_PARENT &&
                    layoutParams.height == ParallaxStackLayout.LayoutParams.MATCH_PARENT) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(false);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setAdjustViewBounds(false);
            }
            switch (imageBean.getGravity()) {
                case "center_top":
                    layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                case "center_bottom":
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    break;
                case "center_left":
                    layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    break;
                case "center_right":
                    layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                    break;
                case "top_left":
                    layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                    break;
                case "bottom_left":
                    layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                    break;
                case "top_right":
                    layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
                    break;
                case "bottom_right":
                    layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                    break;
                case "center":
                    layoutParams.gravity = Gravity.CENTER;
                    break;
                default:
                    break;
            }
            layoutParams.leftMargin = (int) (0.5F + DisplayInfoUtils.getInstance().dp2px(imageBean.getMarginLeft()));
            layoutParams.rightMargin = (int) (0.5F + DisplayInfoUtils.getInstance().dp2px(imageBean.getMarginRight()));
            layoutParams.topMargin = (int) (0.5F + DisplayInfoUtils.getInstance().dp2px(imageBean.getMarginTop()));
            layoutParams.bottomMargin = (int) (0.5F + DisplayInfoUtils.getInstance().dp2px(imageBean.getMarginBottom()));
            layoutParams.setDeviationX(DisplayInfoUtils.getInstance().dp2px(imageBean.getDeviationX()));
            layoutParams.setDeviationY(DisplayInfoUtils.getInstance().dp2px(imageBean.getDeviationY()));
            layoutParams.setRotationX(imageBean.getRotationX());
            layoutParams.setRotationY(imageBean.getRotationY());
            layoutParams.setRotationZ(imageBean.getRotationZ());
            psl.addView(imageView, layoutParams);
            GlideHelper.with(getActivity()).load(imageBean.getUrl()).into(imageView);
        }
    }

    private void startCloseAnimator() {
        closeAnimator = ValueAnimator.ofInt(advertBean.getDuration(), 0);
        closeAnimator.setInterpolator(new LinearInterpolator());
        closeAnimator.setDuration(advertBean.getDuration());
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MaterialProgressBar pb_close = requireView(R.id.dialog_advert_pb_close);
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
