package per.goweii.wanandroid.module.mine.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.db.model.ReadRecordModel;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public interface ReadRecordView extends BaseView {
    void getReadRecordListSuccess(List<ReadRecordModel> list);

    void getReadRecordListFailed();

    void removeReadRecordSuccess(String link);

    void removeReadRecordFailed();

    void removeAllReadRecordSuccess();

    void removeAllReadRecordFailed();
}
