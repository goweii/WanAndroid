package per.goweii.basic.core.base;

import android.content.Context;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import per.goweii.basic.core.mvp.MvpLayer;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/3/29
 */
public abstract class BaseLayer<P extends BasePresenter> extends MvpLayer<P> {
    private Unbinder mUnbinder = null;

    public BaseLayer(Context context) {
        super(context);
    }

    @Override
    protected void initialize() {
        if (mAnyLayer != null && mAnyLayer.getContentView() != null) {
            mUnbinder = ButterKnife.bind(mAnyLayer.getContentView());
        }
        super.initialize();
    }

    @Override
    protected boolean onDestroy() {
        boolean destroy = super.onDestroy();
        if (destroy && mUnbinder != null) {
            mUnbinder.unbind();
        }
        return destroy;
    }
}
