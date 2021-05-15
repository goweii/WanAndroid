package per.goweii.wanandroid.module.mine.adapter

import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import per.goweii.basic.utils.StringUtils
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.mine.model.NotificationBean
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/5/16
 */
class NotificationHistoryAdapter : BaseQuickAdapter<NotificationBean, BaseViewHolder>(
        R.layout.rv_item_notification_history
) {

    private val mUnCloseList: ArrayList<SwipeLayout> = ArrayList()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    closeAll(null)
                }
            }
        })
    }

    fun closeAll(layout: SwipeLayout?) {
        for (swipeLayout in mUnCloseList) {
            if (layout === swipeLayout) {
                continue
            }
            if (swipeLayout.openStatus != SwipeLayout.Status.Open) {
                continue
            }
            swipeLayout.close()
        }
    }

    override fun convert(helper: BaseViewHolder, item: NotificationBean) {
        val sl = helper.getView<SwipeLayout>(R.id.sl)
        sl.addSwipeListener(object : SwipeListener {
            override fun onStartOpen(layout: SwipeLayout) {
                closeAll(layout)
            }

            override fun onOpen(layout: SwipeLayout) {
                mUnCloseList.add(layout)
            }

            override fun onStartClose(layout: SwipeLayout) {}
            override fun onClose(layout: SwipeLayout) {
                mUnCloseList.remove(layout)
            }

            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
        })
        helper.addOnClickListener(R.id.rl_notification, R.id.tv_delete)

        if (item.tags.isEmpty()) {
            helper.setGone(R.id.tv_tag, false)
        } else {
            helper.setGone(R.id.tv_tag, true)
            helper.setText(R.id.tv_tag, item.tags[0])
        }
        helper.setText(R.id.tv_user, item.fromUser)
        helper.setText(R.id.tv_data, item.aniceDate)
        helper.setText(R.id.tv_detail, item.articleContent)
        var content: String = Html.fromHtml(item.content).toString()
        content = StringUtils.removeAllBank(content, 2)
        helper.setGone(R.id.tv_content, content.isNotEmpty())
        helper.setText(R.id.tv_content, content)
    }
}