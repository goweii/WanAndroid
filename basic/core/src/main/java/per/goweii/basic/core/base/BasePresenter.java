package per.goweii.basic.core.base;

import io.reactivex.disposables.Disposable;
import per.goweii.basic.core.mvp.MvpPresenter;
import per.goweii.rxhttp.core.RxLife;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/3/29
 */
public class BasePresenter<V extends BaseView> extends MvpPresenter<V> {
    private RxLife rxLife;

    @Override
    public void attach(V baseView) {
        super.attach(baseView);
        rxLife = RxLife.create();
    }

    @Override
    public void detach() {
        super.detach();
        rxLife.destroy();
        rxLife = null;
    }

    public RxLife getRxLife() {
        return rxLife;
    }

    public void addToRxLife(Disposable disposable) {
        if (rxLife != null) {
            rxLife.add(disposable);
        }
    }
}
