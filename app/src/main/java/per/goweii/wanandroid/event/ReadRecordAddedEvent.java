package per.goweii.wanandroid.event;

import io.reactivex.annotations.NonNull;
import per.goweii.wanandroid.db.model.ReadRecordModel;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public class ReadRecordAddedEvent extends BaseEvent {
    private final ReadRecordModel readRecordModel;

    public ReadRecordAddedEvent(@NonNull ReadRecordModel readRecordModel) {
        this.readRecordModel = readRecordModel;
    }

    @NonNull
    public ReadRecordModel getReadRecordModel() {
        return readRecordModel;
    }
}
