package per.goweii.wanandroid.utils.ad;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qq.e.ads.nativ.NativeExpressADView;

import per.goweii.wanandroid.module.main.adapter.ArticleAdapter;

/**
 * @author CuiZhen
 * @date 2019/12/29
 * GitHub: https://github.com/goweii
 */
public class AdEntity implements MultiItemEntity {
    private int position;
    private NativeExpressADView view;

    @Override
    public int getItemType() {
        return ArticleAdapter.ITEM_TYPE_AD;
    }

    public AdEntity(int position, @Nullable NativeExpressADView view) {
        this.position = position;
        this.view = view;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    public NativeExpressADView getView() {
        return view;
    }

    public void setView(@Nullable NativeExpressADView view) {
        this.view = view;
    }
}
