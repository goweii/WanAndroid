package per.goweii.wanandroid.module.main.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;
import per.goweii.rxhttp.core.RxLife;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.module.main.model.JinrishiciBean;
import per.goweii.wanandroid.module.main.model.MainRequest;

/**
 * @author CuiZhen
 * @date 2019/11/9
 * GitHub: https://github.com/goweii
 */
public class QrcodeShareDialog extends DialogLayer {

    private RxLife mRxLife = null;
    private final String mUrl;
    private final String mTitle;
    private final OnShareClickListener mOnShareClickListener;

    public QrcodeShareDialog(Context context, String url, String title, OnShareClickListener listener) {
        super(context);
        mUrl = url;
        mTitle = title;
        mOnShareClickListener = listener;
        contentView(R.layout.dialog_qrcode_share);
        backgroundDimDefault();
        contentAnimator(new AnimatorCreator() {
            @Override
            public Animator createInAnimator(View target) {
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
            public Animator createOutAnimator(View target) {
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
            public void onClick(Layer layer, View v) {
                if (mOnShareClickListener != null) {
                    mOnShareClickListener.onSave(createCardBitmap());
                }
            }
        }, R.id.dialog_qrcode_share_iv_album);
        onClickToDismiss(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                if (mOnShareClickListener != null) {
                    mOnShareClickListener.onShare(createCardBitmap());
                }
            }
        }, R.id.dialog_qrcode_share_iv_share);
        onClick(new OnClickListener() {
            @Override
            public void onClick(Layer layer, View v) {
                refreshJinrishici();
            }
        }, R.id.dialog_qrcode_share_rv_shici);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        mRxLife = RxLife.create();
        ImageView iv_qrcode = getView(R.id.dialog_qrcode_share_piv_qrcode);
        TextView tv_title = getView(R.id.dialog_qrcode_share_tv_title);
        Bitmap qrcode = CodeUtils.createImage(mUrl, 300, 300, BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_icon));
        iv_qrcode.setImageBitmap(qrcode);
        tv_title.setText(mTitle);
        refreshJinrishici();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRxLife != null) {
            mRxLife.destroy();
        }
    }

    private void refreshJinrishici() {
        MainRequest.getJinrishici(mRxLife, new RequestCallback<JinrishiciBean>() {
            @Override
            public void onSuccess(int code, JinrishiciBean data) {
                TextView tv_shici = getView(R.id.dialog_qrcode_share_tv_shici);
                if (tv_shici != null) {
//                    tv_shici.setText(formatShici(data.getContent()));
                    tv_shici.setText(data.getContent());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                TextView tv_shici = getView(R.id.dialog_qrcode_share_tv_shici);
                if (tv_shici != null) {
                    tv_shici.setText("获取失败，点击刷新");
                }
            }
        });
    }

    private String formatShici(String shici) {
        String[] split = shici.split("[，。；？！]");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if (!TextUtils.isEmpty(s)) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private Bitmap createCardBitmap() {
        View rl_card = getView(R.id.dialog_qrcode_share_rl_card);
        Bitmap bitmap = Bitmap.createBitmap(rl_card.getWidth(), rl_card.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rl_card.draw(canvas);
        return bitmap;
    }

    public interface OnShareClickListener {
        void onSave(Bitmap bitmap);

        void onShare(Bitmap bitmap);
    }
}
