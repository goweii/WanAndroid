package per.goweii.wanandroid.module.mine.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.AppInfoUtils;
import per.goweii.basic.utils.AppOpenUtils;
import per.goweii.basic.utils.CopyUtils;
import per.goweii.basic.utils.bitmap.BitmapUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.http.RequestCallback;
import per.goweii.wanandroid.module.mine.model.AboutMeBean;
import per.goweii.wanandroid.module.mine.model.MineRequest;
import per.goweii.wanandroid.module.mine.view.AboutMeView;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public class AboutMePresenter extends BasePresenter<AboutMeView> {

    private AboutMeBean mAboutMeBean = null;

    public void getAboutMe(){
        MineRequest.getAboutMe(getRxLife(), new RequestCallback<AboutMeBean>() {
            @Override
            public void onSuccess(int code, AboutMeBean data) {
                mAboutMeBean = data;
                if (isAttach()) {
                    getBaseView().getAboutMeSuccess(code, data);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().getAboutMeFailed(code, msg);
                }
            }
        });
    }

    public void openQQChat() {
        if (mAboutMeBean == null) {
            return;
        }
        if (AppInfoUtils.isAppInstalled(getContext(), AppInfoUtils.PackageName.QQ)) {
            if (AppOpenUtils.openQQChat(getContext(), mAboutMeBean.getQq())) {
                return;
            }
        }
        CopyUtils.copyText(mAboutMeBean.getQq());
        ToastMaker.showShort("QQ已复制");
    }

    public void openQQGroup() {
        if (mAboutMeBean == null) {
            return;
        }
        if (AppInfoUtils.isAppInstalled(getContext(), AppInfoUtils.PackageName.QQ)) {
            if (!TextUtils.isEmpty(mAboutMeBean.getQq_group_key())) {
                if (AppOpenUtils.joinQQGroup(getContext(), mAboutMeBean.getQq_group_key())) {
                    return;
                }
            }
        }
        CopyUtils.copyText(mAboutMeBean.getQq_group());
        ToastMaker.showShort("QQ群已复制");
    }

    public void saveQQQrcode() {
        if (mAboutMeBean == null) {
            return;
        }
        GlideHelper.with(getContext())
                .load(mAboutMeBean.getQq_qrcode())
                .get(new SimpleCallback<Bitmap>() {
                    @Override
                    public void onResult(Bitmap data) {
                        if (null != BitmapUtils.saveGallery(data, mAboutMeBean.getName() + "_qq_qrcode_" + System.currentTimeMillis())) {
                            ToastMaker.showShort("保存成功");
                        } else {
                            ToastMaker.showShort("保存失败");
                        }
                    }
                });
    }

    public void saveWXQrcode() {
        if (mAboutMeBean == null) {
            return;
        }
        GlideHelper.with(getContext())
                .load(mAboutMeBean.getWx_qrcode())
                .get(new SimpleCallback<Bitmap>() {
                    @Override
                    public void onResult(Bitmap data) {
                        if (null != BitmapUtils.saveGallery(data, mAboutMeBean.getName() + "_wx_qrcode_" + System.currentTimeMillis())) {
                            ToastMaker.showShort("保存成功");
                        } else {
                            ToastMaker.showShort("保存失败");
                        }
                    }
                });
    }

}
