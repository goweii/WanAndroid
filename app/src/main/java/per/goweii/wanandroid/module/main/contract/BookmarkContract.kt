package per.goweii.wanandroid.module.main.contract

import per.goweii.basic.core.base.BasePresenter
import per.goweii.basic.core.base.BaseView
import per.goweii.wanandroid.db.executor.ReadLaterExecutor
import per.goweii.wanandroid.db.model.ReadLaterModel

/**
 * @author CuiZhen
 * @date 2020/3/22
 */
interface BookmarkView : BaseView {
    fun getBookmarkListSuccess(list: List<ReadLaterModel>)
    fun getBookmarkListFailed()
}

class BookmarkPresenter : BasePresenter<BookmarkView>() {
    private var mReadLaterExecutor: ReadLaterExecutor? = null

    override fun attach(baseView: BookmarkView) {
        super.attach(baseView)
        mReadLaterExecutor = ReadLaterExecutor()
    }

    override fun detach() {
        if (mReadLaterExecutor != null) mReadLaterExecutor!!.destroy()
        super.detach()
    }

    fun getList(offset: Int, perPageCount: Int) {
        mReadLaterExecutor?.getList(offset, perPageCount, {
            if (isAttach) {
                baseView.getBookmarkListSuccess(it)
            }
        }, {
            if (isAttach) {
                baseView.getBookmarkListFailed()
            }
        })
    }
}