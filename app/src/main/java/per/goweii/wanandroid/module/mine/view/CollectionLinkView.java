package per.goweii.wanandroid.module.mine.view;

import java.util.List;

import per.goweii.basic.core.base.BaseView;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * GitHub: https://github.com/goweii
 */
public interface CollectionLinkView extends BaseView {
    void getCollectLinkListSuccess(int code, List<CollectionLinkBean> data);
    void getCollectLinkListFailed(int code, String msg);

    void updateCollectLinkSuccess(int code, CollectionLinkBean data);
}
