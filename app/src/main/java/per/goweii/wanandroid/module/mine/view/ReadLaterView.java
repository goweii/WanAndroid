package per.goweii.wanandroid.module.mine.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.db.model.ReadLaterModel;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public interface ReadLaterView extends BaseView {
    void getReadLaterListSuccess(List<ReadLaterModel> list);

    void getReadLaterListFailed();

    void removeReadLaterSuccess(String link);

    void removeReadLaterFailed();

    void removeAllReadLaterSuccess();

    void removeAllReadLaterFailed();
}
