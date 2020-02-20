package per.goweii.wanandroid.module.mine.model;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.module.main.model.CoinInfoBean;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * GitHub: https://github.com/goweii
 */
public class CoinRankBean extends BaseBean {
    /**
     * curPage : 1
     * datas : [{"coinCount":513,"username":"S**24n"},{"coinCount":450,"username":"1**08491840"},{"coinCount":450,"username":"r**one"},{"coinCount":450,"username":"g**eii"},{"coinCount":420,"username":"陈**啦啦啦"},{"coinCount":420,"username":"j**gbin"},{"coinCount":420,"username":"j**ksen"},{"coinCount":420,"username":"c**huah"},{"coinCount":420,"username":"x**oyang"},{"coinCount":419,"username":"l**64301766"},{"coinCount":390,"username":"S**iWanZi"},{"coinCount":390,"username":"w**.wanandroid.com"},{"coinCount":390,"username":"l**changwen"},{"coinCount":387,"username":"w**yyy"},{"coinCount":361,"username":"c**yie"},{"coinCount":361,"username":"l**hehan"},{"coinCount":361,"username":"c**01220122"},{"coinCount":361,"username":"7**502274@qq.com"},{"coinCount":361,"username":"o**shot"},{"coinCount":361,"username":"w**lwaywang6"},{"coinCount":361,"username":"l**kyli"},{"coinCount":361,"username":"a**ian"},{"coinCount":333,"username":"1**88468150"},{"coinCount":333,"username":"2**339961@qq.com"},{"coinCount":333,"username":"i**Cola"},{"coinCount":333,"username":"l**shifu"},{"coinCount":333,"username":"猥**豆腐6"},{"coinCount":333,"username":"8**408834@qq.com"},{"coinCount":333,"username":"1**23822235"},{"coinCount":333,"username":"z**ing"}]
     * offset : 0
     * over : false
     * pageCount : 87
     * size : 30
     * total : 2592
     */

    private int curPage;
    private int offset;
    private boolean over;
    private int pageCount;
    private int size;
    private int total;
    private List<CoinInfoBean> datas;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CoinInfoBean> getDatas() {
        return datas;
    }

    public void setDatas(List<CoinInfoBean> datas) {
        this.datas = datas;
    }
}
