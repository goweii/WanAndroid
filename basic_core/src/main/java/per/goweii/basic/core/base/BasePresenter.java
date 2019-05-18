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
    protected void onCreate(V baseView) {
        super.onCreate(baseView);
        rxLife = RxLife.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
