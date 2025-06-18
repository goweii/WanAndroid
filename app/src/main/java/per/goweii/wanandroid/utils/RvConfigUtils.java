package per.goweii.wanandroid.utils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.widget.BravhLoadMoreView;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
@Deprecated
public class RvConfigUtils {

    public static class RvAnim {
        public static final int NONE = 0;
        public static final int ALPHAIN = 1;
        public static final int SCALEIN = 2;
        public static final int SLIDEIN_BOTTOM = 3;
        public static final int SLIDEIN_LEFT = 4;
        public static final int SLIDEIN_RIGHT = 5;
    }

    public static String getName(int anim) {
        String name = "";
        switch (anim) {
            default:
                break;
            case RvAnim.NONE:
                name = ResUtils.getString(R.string.rv_anim_none);
                break;
            case RvAnim.ALPHAIN:
                name = ResUtils.getString(R.string.rv_anim_alpha_in);
                break;
            case RvAnim.SCALEIN:
                name = ResUtils.getString(R.string.rv_anim_zoom);
                break;
            case RvAnim.SLIDEIN_BOTTOM:
                name = ResUtils.getString(R.string.rv_anim_slide_in_bottom);
                break;
            case RvAnim.SLIDEIN_LEFT:
                name = ResUtils.getString(R.string.rv_anim_slide_in_left);
                break;
            case RvAnim.SLIDEIN_RIGHT:
                name = ResUtils.getString(R.string.rv_anim_slide_in_right);
                break;
        }
        return name;
    }

    public static void init(BaseQuickAdapter<?, ? extends BaseViewHolder> adapter) {
        adapter.setLoadMoreView(new BravhLoadMoreView());
    }

    public static void setAnim(BaseQuickAdapter<?, ? extends BaseViewHolder> adapter, int anim) {
        switch (anim) {
            default:
                break;
            case RvAnim.NONE:
                adapter.closeLoadAnimation();
                break;
            case RvAnim.ALPHAIN:
                adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
                break;
            case RvAnim.SCALEIN:
                adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                break;
            case RvAnim.SLIDEIN_BOTTOM:
                adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
                break;
            case RvAnim.SLIDEIN_LEFT:
                adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
                break;
            case RvAnim.SLIDEIN_RIGHT:
                adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                break;
        }
    }
}
