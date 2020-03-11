package per.goweii.wanandroid.module.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.mine.model.CoinRecordBean;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class CoinRecordAdapter extends BaseQuickAdapter<CoinRecordBean.DatasBean, BaseViewHolder> {

    public CoinRecordAdapter() {
        super(R.layout.rv_item_coin_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinRecordBean.DatasBean item) {
        String desc = item.getDesc();
        int firstSpace = desc.indexOf(" ");
        int secondSpace = desc.indexOf(" ", firstSpace + 1);
        String time = desc.substring(0, secondSpace);
        String title = desc.substring(secondSpace + 1)
                .replace(",", "")
                .replace("：", "")
                .replace(" ", "");
        helper.setText(R.id.tv_coin_count, "+" + item.getCoinCount());
        helper.setText(R.id.tv_title, title);
        helper.setText(R.id.tv_time, time);
    }
}
