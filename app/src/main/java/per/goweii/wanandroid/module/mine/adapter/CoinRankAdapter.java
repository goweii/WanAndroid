package per.goweii.wanandroid.module.mine.adapter;

import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import per.goweii.wanandroid.R;
import per.goweii.wanandroid.module.mine.model.CoinRankBean;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CoinRankAdapter extends BaseQuickAdapter<CoinRankBean.DatasBean, BaseViewHolder> {

    public CoinRankAdapter() {
        super(R.layout.rv_item_coin_rank);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinRankBean.DatasBean item) {
        int index = helper.getAdapterPosition() + 1;
        helper.setText(R.id.tv_index, "" + index);
        helper.setText(R.id.tv_user_name, item.getUsername());
        helper.setText(R.id.tv_coin_count, "" + item.getCoinCount());
        ImageView iv_index = helper.getView(R.id.iv_index);
        TextView tv_index = helper.getView(R.id.tv_index);
        if (index == 1) {
            iv_index.setImageResource(R.drawable.ic_rank_1);
            tv_index.setTextColor(ContextCompat.getColor(tv_index.getContext(), R.color.text_white_alpha));
            tv_index.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv_index.getContext().getResources().getDimension(R.dimen.text_auxiliary));
        } else if (index == 2) {
            iv_index.setImageResource(R.drawable.ic_rank_2);
            tv_index.setTextColor(ContextCompat.getColor(tv_index.getContext(), R.color.text_white_alpha));
            tv_index.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv_index.getContext().getResources().getDimension(R.dimen.text_auxiliary));
        } else if (index == 3) {
            iv_index.setImageResource(R.drawable.ic_rank_3);
            tv_index.setTextColor(ContextCompat.getColor(tv_index.getContext(), R.color.text_white_alpha));
            tv_index.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv_index.getContext().getResources().getDimension(R.dimen.text_auxiliary));
        } else {
            iv_index.setImageResource(R.color.transparent);
            tv_index.setTextColor(ContextCompat.getColor(tv_index.getContext(), R.color.text_gray_light));
            tv_index.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv_index.getContext().getResources().getDimension(R.dimen.text_content));
        }
    }
}
