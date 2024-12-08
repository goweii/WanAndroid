package per.goweii.basic.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.widget.SwipeLayout;
import per.goweii.basic.ui.R;
import per.goweii.basic.utils.ResUtils;

/**
 * @author CuiZhen
 * @date 2019/5/20
 * GitHub: https://github.com/goweii
 */
public class ListDialog {

    private final Context context;
    private CharSequence title;
    private CharSequence yesText;
    private CharSequence noText;
    private boolean noBtn = false;
    private boolean singleBtnYes = false;
    private boolean cancelable = true;
    private OnItemSelectedListener listener = null;
    private BaseQuickAdapter<String, BaseViewHolder> mAdapter = null;
    private List<String> datas = new ArrayList<>();
    private int currSelectPos = -1;

    public static ListDialog with(Context context) {
        return new ListDialog(context);
    }

    private ListDialog(Context context) {
        this.context = context;
    }

    public ListDialog title(CharSequence title) {
        this.title = title;
        return this;
    }

    public ListDialog title(@StringRes int title) {
        this.title = context.getString(title);
        return this;
    }

    public ListDialog yesText(CharSequence yesText) {
        this.yesText = yesText;
        return this;
    }

    public ListDialog yesText(@StringRes int yesText) {
        this.yesText = context.getString(yesText);
        return this;
    }

    public ListDialog noText(CharSequence noText) {
        this.noText = noText;
        return this;
    }

    public ListDialog noText(@StringRes int noText) {
        this.noText = context.getString(noText);
        return this;
    }

    public ListDialog noBtn() {
        noBtn = true;
        return this;
    }

    public ListDialog singleYesBtn() {
        singleBtnYes = true;
        return this;
    }

    public ListDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public ListDialog datas(List<String> datas) {
        this.datas.addAll(datas);
        return this;
    }

    public ListDialog datas(String... datas) {
        return datas(Arrays.asList(datas));
    }

    public ListDialog currSelectPos(int currSelectPos) {
        this.currSelectPos = currSelectPos;
        return this;
    }

    public ListDialog listener(OnItemSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    public void show() {
        AnyLayer.dialog(context)
                .setContentView(R.layout.basic_ui_dialog_list)
                .setGravity(Gravity.BOTTOM)
                .setBackgroundDimDefault()
                .setSwipeDismiss(SwipeLayout.Direction.BOTTOM)
                .setCancelableOnTouchOutside(cancelable)
                .setCancelableOnClickKeyBack(cancelable)
                .addOnBindDataListener(new Layer.OnBindDataListener() {
                    @Override
                    public void onBindData(@NonNull Layer layer) {
                        LinearLayout llYesNo = layer.requireView(R.id.basic_ui_ll_dialog_list_yes_no);
                        View vLineH = layer.requireView(R.id.basic_ui_v_dialog_list_line_h);

                        if (noBtn) {
                            vLineH.setVisibility(View.GONE);
                            llYesNo.setVisibility(View.GONE);
                        } else {
                            vLineH.setVisibility(View.VISIBLE);
                            llYesNo.setVisibility(View.VISIBLE);
                            TextView tvYes = layer.requireView(R.id.basic_ui_tv_dialog_list_yes);
                            TextView tvNo = layer.requireView(R.id.basic_ui_tv_dialog_list_no);
                            View vLine = layer.requireView(R.id.basic_ui_v_dialog_list_line);
                            if (yesText != null) {
                                tvYes.setText(yesText);
                            } else {
                                tvYes.setText(R.string.basic_ui_dialog_btn_yes);
                            }
                            if (singleBtnYes) {
                                tvNo.setVisibility(View.GONE);
                                vLine.setVisibility(View.GONE);
                            } else {
                                tvNo.setVisibility(View.VISIBLE);
                                vLine.setVisibility(View.VISIBLE);
                                if (noText != null) {
                                    tvNo.setText(noText);
                                } else {
                                    tvNo.setText(R.string.basic_ui_dialog_btn_no);
                                }
                            }
                        }

                        TextView tvTitle = layer.requireView(R.id.basic_ui_tv_dialog_list_title);
                        View vTitleLineH = layer.requireView(R.id.basic_ui_v_dialog_title_line_h);
                        if (title == null) {
                            tvTitle.setVisibility(View.GONE);
                            vTitleLineH.setVisibility(View.GONE);
                        } else {
                            tvTitle.setText(title);
                            vTitleLineH.setVisibility(View.VISIBLE);
                        }

                        RecyclerView rv = layer.requireView(R.id.basic_ui_rv_dialog_list);
                        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                        mAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.basic_ui_rv_item_dialog_list) {
                            @Override
                            protected void convert(BaseViewHolder helper, String item) {
                                TextView tvName = helper.getView(R.id.basic_ui_tv_dialog_list_name);
                                if (helper.getAdapterPosition() == currSelectPos) {
                                    tvName.setTextColor(ResUtils.getThemeColor(tvName.getContext(), R.attr.colorTextMain));
                                } else {
                                    tvName.setTextColor(ResUtils.getThemeColor(tvName.getContext(), R.attr.colorTextSurface));
                                }
                                tvName.setText(item);
                            }
                        };
                        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                currSelectPos = position;
                                adapter.notifyDataSetChanged();
                                if (noBtn) {
                                    if (listener != null) {
                                        listener.onSelect(datas.get(currSelectPos), currSelectPos);
                                    }
                                    layer.dismiss();
                                }
                            }
                        });
                        rv.setAdapter(mAdapter);
                        mAdapter.setNewData(datas);
                    }
                })
                .addOnClickToDismissListener(new Layer.OnClickListener() {
                    @Override
                    public void onClick(@NonNull Layer layer, @NonNull View v) {
                        if (listener != null) {
                            listener.onSelect(datas.get(currSelectPos), currSelectPos);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_list_yes)
                .addOnClickToDismissListener(R.id.basic_ui_tv_dialog_list_no)
                .show();
    }

    public interface OnItemSelectedListener {
        void onSelect(String data, int pos);
    }
}
