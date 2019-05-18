package per.goweii.wanandroid.module.home.model;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.module.main.model.ArticleBean;

/**
 * @author CuiZhen
 * @date 2019/5/18
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class SearchBean extends BaseBean {
    /**
     * curPage : 1
     * datas : [{"apkLink":"","author":"996icu","chapterId":296,"chapterName":"阅读","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8167,"link":"https://github.com/996icu/996.ICU","niceDate":"2019-03-29","origin":"","prefix":"","projectLink":"","publishTime":1553865529000,"superChapterId":202,"superChapterName":"延伸技术","tags":[],"title":"<em class='highlight'>996<\/em>.ICU","type":0,"userId":-1,"visible":0,"zan":0}]
     * offset : 0
     * over : true
     * pageCount : 1
     * size : 20
     * total : 1
     */

    private int curPage;
    private int offset;
    private boolean over;
    private int pageCount;
    private int size;
    private int total;
    private List<ArticleBean> datas;

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

    public List<ArticleBean> getDatas() {
        return datas;
    }

    public void setDatas(List<ArticleBean> datas) {
        this.datas = datas;
    }

}
