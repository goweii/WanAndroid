package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.dialog.DialogLayer;
import per.goweii.anylayer.utils.AnimatorHelper;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.codex.encoder.CodeEncoder;
import per.goweii.codex.processor.zxing.ZXingEncodeQRCodeProcessor;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/11/9
 * GitHub: https://github.com/goweii
 */
public class QrcodeShareDialog extends DialogLayer {

    private RxLife mRxLife = null;
    private final String mUrl;
    private final String mTitle;
    private SimpleCallback<Bitmap> mOnAlbumClickListener = null;
    private SimpleCallback<Bitmap> mOnShareClickListener = null;

    public QrcodeShareDialog(Context context, String url, String title) {
        super(context);
        mUrl = url;
        mTitle = title;
        contentView(R.layout.dialog_qrcode_share);
        backgroundDimDefault();
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(@NonNull View target) {
                View rl_card = getView(R.id.dialog_qrcode_share_rl_card);
                View rl_btn = getView(R.id.dialog_qrcode_share_ll_btn);
                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(
                        AnimatorHelper.createAlphaInAnim(rl_card),
                        AnimatorHelper.createBottomInAnim(rl_btn)
                );
                return animator;
            }

            @Override
            public Animator createOutAnimator(@NonNull View target) {
                View rl_card = getView(R.id.dialog_qrcode_share_rl_card);
                View rl_btn = getView(R.id.dialog_qrcode_share_ll_btn);
                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(
                        AnimatorHelper.createAlphaOutAnim(rl_card),
                        AnimatorHelper.createBottomOutAnim(rl_btn)
                );
                return animator;
            }
        });
        onClickToDismiss(R.id.dialog_qrcode_share_rl_content);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                if (mOnAlbumClickListener != null) {
                    mOnAlbumClickListener.onResult(createCardBitmap());
                }
            }
        }, R.id.dialog_qrcode_share_iv_album);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(@NonNull Layer layer, @NonNull View v) {
                if (mOnShareClickListener != null) {
                    mOnShareClickListener.onResult(createCardBitmap());
                }
            }
        }, R.id.dialog_qrcode_share_iv_share);
    }

    public QrcodeShareDialog setOnAlbumClickListener(SimpleCallback<Bitmap> mOnAlbumClickListener) {
        this.mOnAlbumClickListener = mOnAlbumClickListener;
        return this;
    }

    public QrcodeShareDialog setOnShareClickListener(SimpleCallback<Bitmap> mOnShareClickListener) {
        this.mOnShareClickListener = mOnShareClickListener;
        return this;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        mRxLife = RxLife.create();
        LinearLayout ll_album = getView(R.id.dialog_qrcode_share_ll_album);
        LinearLayout ll_share = getView(R.id.dialog_qrcode_share_ll_share);
        if (mOnAlbumClickListener == null) {
            ll_album.setVisibility(View.GONE);
        } else {
            ll_album.setVisibility(View.VISIBLE);
        }
        if (mOnShareClickListener == null) {
            ll_share.setVisibility(View.GONE);
        } else {
            ll_share.setVisibility(View.VISIBLE);
        }
        ImageView iv_qrcode = getView(R.id.dialog_qrcode_share_piv_qrcode);
        TextView tv_title = getView(R.id.dialog_qrcode_share_tv_title);
        tv_title.setText(mTitle);
        new CodeEncoder(new ZXingEncodeQRCodeProcessor()).encode(mUrl, new Function1<Bitmap, Unit>() {
            @Override
            public Unit invoke(Bitmap bitmap) {
                iv_qrcode.setImageBitmap(bitmap);
                return null;
            }
        }, new Function1<Exception, Unit>() {
            @Override
            public Unit invoke(Exception e) {
                return null;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRxLife != null) {
            mRxLife.destroy();
        }
    }

    private Bitmap createCardBitmap() {
        View rl_card = getView(R.id.dialog_qrcode_share_rl_card);
        Bitmap bitmap = Bitmap.createBitmap(rl_card.getWidth(), rl_card.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rl_card.draw(canvas);
        return bitmap;
    }
}
