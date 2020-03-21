package per.goweii.wanandroid.module.mine.presenter;

import java.util.List;

import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.basic.utils.listener.SimpleListener;
import per.goweii.wanandroid.db.executor.ReadLaterExecutor;
import per.goweii.wanandroid.db.model.ReadLaterModel;
import per.goweii.wanandroid.module.mine.view.ReadLaterView;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class ReadLaterPresenter extends BasePresenter<ReadLaterView> {

    private ReadLaterExecutor mReadLaterExecutor = null;

    @Override
    public void attach(ReadLaterView baseView) {
        super.attach(baseView);
        mReadLaterExecutor = new ReadLaterExecutor();
    }

    @Override
    public void detach() {
        if (mReadLaterExecutor != null) mReadLaterExecutor.destroy();
        super.detach();
    }

    public void getList(int offset, int perPageCount) {
        if (mReadLaterExecutor == null) return;
        mReadLaterExecutor.getList(offset, perPageCount, new SimpleCallback<List<ReadLaterModel>>() {
            @Override
            public void onResult(List<ReadLaterModel> data) {
                if (isAttach()) {
                    getBaseView().getReadLaterListSuccess(data);
                }
            }
        }, new SimpleListener() {
            @Override
            public void onResult() {
                if (isAttach()) {
                    getBaseView().getReadLaterListFailed();
                }
            }
        });
    }

    public void remove(String link) {
        if (mReadLaterExecutor == null) return;
        mReadLaterExecutor.remove(link, new SimpleListener() {
            @Override
            public void onResult() {
                if (isAttach()) {
                    getBaseView().removeReadLaterSuccess(link);
                }
            }
        }, new SimpleListener() {
            @Override
            public void onResult() {
                if (isAttach()) {
                    getBaseView().removeReadLaterFailed();
                }
            }
        });
    }

    public void removeAll() {
        if (mReadLaterExecutor == null) return;
        mReadLaterExecutor.removeAll(new SimpleListener() {
            @Override
            public void onResult() {
                if (isAttach()) {
                    getBaseView().removeAllReadLaterSuccess();
                }
            }
        }, new SimpleListener() {
            @Override
            public void onResult() {
                if (isAttach()) {
                    getBaseView().removeAllReadLaterFailed();
                }
            }
        });
    }
}
