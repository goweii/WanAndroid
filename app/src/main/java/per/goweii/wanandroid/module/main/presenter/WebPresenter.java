package per.goweii.wanandroid.module.main.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.core.glide.GlideHelper;
import per.goweii.basic.ui.toast.ToastMaker;
import per.goweii.basic.utils.bitmap.BitmapUtils;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.event.CollectionEvent;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.CollectArticleEntity;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.main.model.MainRequest;
import per.goweii.wanandroid.module.main.view.WebView;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public class WebPresenter extends BasePresenter<WebView> {

    public void collect(CollectArticleEntity entity) {
        if (entity.isCollect()) {
            if (isAttach()) {
                getBaseView().collectSuccess(entity);
            }
            return;
        }
        if (entity.getArticleId() > 0) {
            collect(entity.getArticleId(), entity);
        } else {
            if (TextUtils.isEmpty(entity.getAuthor())) {
                collect(entity.getTitle(), entity.getUrl(), entity);
            } else {
                collect(entity.getTitle(), entity.getAuthor(), entity.getUrl(), entity);
            }
        }
    }

    public void uncollect(CollectArticleEntity entity) {
        if (!entity.isCollect()) {
            if (isAttach()) {
                getBaseView().uncollectSuccess(entity);
            }
            return;
        }
        if (entity.getArticleId() > 0) {
            uncollect(entity.getArticleId(), entity);
        } else {
            uncollectLink(entity.getCollectId(), entity);
        }
    }

    private void collect(int id, final CollectArticleEntity entity) {
        addToRxLife(MainRequest.collect(id, new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postCollectWithArticleId(id);
                entity.setCollect(true);
                if (isAttach()) {
                    getBaseView().collectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().collectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        }));
    }

    private void uncollect(final int id, final CollectArticleEntity entity) {
        addToRxLife(MainRequest.uncollect(id, new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postUnCollectWithArticleId(id);
                entity.setCollect(false);
                if (isAttach()) {
                    getBaseView().uncollectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().uncollectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        }));
    }

    private void collect(String title, String author, String link, CollectArticleEntity entity) {
        addToRxLife(MainRequest.collect(title, author, link, new RequestListener<ArticleBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, ArticleBean data) {
                CollectionEvent.postCollectWithCollectId(data.getId());
                entity.setCollect(true);
                if (isAttach()) {
                    getBaseView().collectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().collectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        }));
    }

    private void collect(String title, String link, CollectArticleEntity entity) {
        addToRxLife(MainRequest.collect(title, link, new RequestListener<CollectionLinkBean>() {
            @Override
            public void onStart() {
                showLoadingBar();
            }

            @Override
            public void onSuccess(int code, CollectionLinkBean data) {
                CollectionEvent.postCollectWithCollectId(data.getId());
                entity.setCollect(true);
                if (isAttach()) {
                    getBaseView().collectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().collectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
                dismissLoadingBar();
            }
        }));
    }

    private void uncollectLink(int id, CollectArticleEntity entity) {
        addToRxLife(MainRequest.uncollectLink(id, new RequestListener<BaseBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, BaseBean data) {
                CollectionEvent.postUncollectWithCollectId(id);
                entity.setCollect(false);
                if (isAttach()) {
                    getBaseView().uncollectSuccess(entity);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                if (isAttach()) {
                    getBaseView().uncollectFailed(msg);
                }
            }

            @Override
            public void onError(ExceptionHandle handle) {
            }

            @Override
            public void onFinish() {
            }
        }));
    }

    public void saveGallery(Bitmap bitmap, String name) {
        if (null != BitmapUtils.saveGallery(bitmap, name)) {
            ToastMaker.showShort("以保存到相册");
        } else {
            ToastMaker.showShort("保存失败");
        }
        bitmap.recycle();
    }

    public void createQrcodeImage(Bitmap qrcode, String title, SimpleCallback<Bitmap> callback) {
        SimpleCallback<View> inflateCallback = new SimpleCallback<View>() {
            @Override
            public void onResult(View view) {
                int w = View.MeasureSpec.makeMeasureSpec(qrcode.getWidth(), View.MeasureSpec.EXACTLY);
                int h = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
                view.measure(w, h);
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                qrcode.recycle();
                callback.onResult(bitmap);
            }
        };
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_web_share_qrcode, null);
        ImageView piv_qrcode = view.findViewById(R.id.piv_qrcode);
        piv_qrcode.setImageBitmap(qrcode);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(title);
        RelativeLayout rl_user_info = view.findViewById(R.id.rl_user_info);
        if (UserUtils.getInstance().isLogin()) {
            rl_user_info.setVisibility(View.VISIBLE);
            TextView tv_user_name = view.findViewById(R.id.tv_user_name);
            ImageView civ_user_icon = view.findViewById(R.id.civ_user_icon);
            tv_user_name.setText(UserUtils.getInstance().getLoginBean().getUsername() +
                    "(" + UserUtils.getInstance().getLoginBean().getId() + ")");
            GlideHelper.with(getContext())
                    .load(UserUtils.getInstance().getLoginBean().getIcon())
                    .get(new SimpleCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap data) {
                            civ_user_icon.setImageBitmap(data);
                            inflateCallback.onResult(view);
                        }
                    }, new SimpleListener() {
                        @Override
                        public void onResult() {
                            civ_user_icon.setImageDrawable(new ColorDrawable(Color.parseColor("#22000000")));
                            inflateCallback.onResult(view);
                        }
                    });
        } else {
            rl_user_info.setVisibility(View.GONE);
            inflateCallback.onResult(view);
        }
    }
}
