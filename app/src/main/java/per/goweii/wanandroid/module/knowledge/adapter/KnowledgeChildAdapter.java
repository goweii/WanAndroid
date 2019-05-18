package per.goweii.wanandroid.module.knowledge.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.knowledge.model.KnowledgeBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class KnowledgeChildAdapter extends BaseQuickAdapter<KnowledgeBean, BaseViewHolder> {

    public KnowledgeChildAdapter() {
        super(R.layout.rv_item_knowledge_child);
    }

    @Override
    protected void convert(BaseViewHolder helper, KnowledgeBean item) {
        helper.setText(R.id.tv_name, item.getName());
    }
}
